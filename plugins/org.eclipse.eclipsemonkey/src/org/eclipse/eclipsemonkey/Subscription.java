/*******************************************************************************
 * Copyright (c) 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.core.runtime.IPath;

/**
 * 
 */
public class Subscription {

	private final String addMethodName;

	private Object listenerProxy;

	private Method removeMethod;

	private Object source;

	private String sourceString;

	/**
	 * 
	 */
	public String trouble = null;

	/**
	 * 
	 * @param _sourceString
	 * @param addMethodName
	 */
	public Subscription(String _sourceString, String addMethodName) {
		this.sourceString = _sourceString;
		this.addMethodName = addMethodName;
	}

	/**
	 * 
	 * @param path
	 */
	public void subscribe(IPath path) {
		try {
			MenuRunMonkeyScript runner = new MenuRunMonkeyScript(path);
			source = runner.run(this.sourceString, new Object[0], false);
			subscribe(path, source, addMethodName);
		} catch (Throwable e) {
			trouble(e);
		}
	}

	/**
	 * 
	 * @param e
	 */
	protected void trouble(Throwable e) {
		if (e instanceof RunMonkeyException) {
			RunMonkeyException x = (RunMonkeyException) e;
			trouble = x.errorMessage + x.optionalLineNumber();
		} else {
			trouble = e.getMessage();
		}
		EclipseMonkeyPlugin.getDefault().notifyScriptsChanged();
	}

	/**
	 * 
	 *
	 */
	public void unsubscribe() {
		if (trouble != null || removeMethod == null)
			return;
		try {
			removeMethod.invoke(source, new Object[] { listenerProxy });
			removeMethod = null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void subscribe(IPath path, Object foo, String methodName)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		InvocationHandler listener = new ScriptSubscriptionListener(path,
				this);
		Method addMethod = findAddMethod(foo, methodName);

		Class listenerType = addMethod.getParameterTypes()[0];
		listenerProxy = Proxy.newProxyInstance(listenerType.getClassLoader(),
				new Class[] { listenerType }, listener);
		addMethod.invoke(foo, new Object[] { listenerProxy });
	}

	private Method findAddMethod(Object source, String methodName)
			throws NoSuchMethodException {
		Method methods[] = source.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];
			if (!(m.getName().equals(methodName)))
				continue;
			if (m.getParameterTypes().length != 1)
				continue;
			if (findRemoveMethod(source, methodName, m.getParameterTypes()) == null)
				continue;

			return m;
		}
		throw new NoSuchMethodException(Messages.Subscription_ERR_MSG_Cant_find_add_method);
	}

	private Method findRemoveMethod(Object source, String methodName,
			Class[] parameterTypes) {
		String removeMethodName = "remove" + methodName.substring(3); //$NON-NLS-1$
		try {
			removeMethod = source.getClass().getMethod(removeMethodName,
					parameterTypes);
			return removeMethod;
		} catch (Exception e) {
			return null;
		}
	}

	class ScriptSubscriptionListener implements InvocationHandler {
		IPath script;
		Subscription subscription;

		/**
		 * @param script
		 * @param subscription
		 */
		public ScriptSubscriptionListener(IPath script,
				Subscription subscription) {
			this.script = script;
			this.subscription = subscription;
		}

		/**
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			/*
			 * we do NOT want the ctrl-shift-M "re-run the last script" accelerator
			 * bound to this script.
			 */
			try {
				MenuRunMonkeyScript runner = new MenuRunMonkeyScript(script);
				return runner.run(method.getName(), args, false);
			} catch (RunMonkeyException x) {
				subscription.unsubscribe();
				subscription.trouble(x);
				return null;
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (trouble == null ? "" : trouble + ": ") + this.sourceString //$NON-NLS-1$ //$NON-NLS-2$
				+ "()." + this.addMethodName //$NON-NLS-1$
				+ (removeMethod == null ? " (not listening)" : ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}