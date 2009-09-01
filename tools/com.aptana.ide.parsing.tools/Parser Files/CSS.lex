%option case-insensitive

h			[0-9a-f]
nonascii	[\200-\377]
unicode		\\{h}{1,6}(\r\n|[ \t\r\n\f])?
escape		{unicode}|\\[ -~\200-\377]
nmstart		[_a-z]|{nonascii}|{escape}
nmchar		[_a-zA-Z0-9-]|{nonascii}|{escape}
string1		\"([\t !#$%&(-~]|\\{nl}|\’|{nonascii}|{escape})*\"
string2		\’([\t !#$%&(-~]|\\{nl}|\"|{nonascii}|{escape})*\’
ident		{nmstart}{nmchar}*
name		{nmchar}+
num			[0-9]+|[0-9]*"."[0-9]+
string		{string1}|{string2}
url			([!#$%&*-~]|{nonascii}|{escape})*
s			[ \t\r\n\f]
w			{s}*
nl			\n|\r\n|\r|\f
range		\?{1,6}|{h}(\?{0,5}|{h}(\?{0,4}|{h}(\?{0,3}|{h}(\?{0,2}|{h}(\??|{h})))))

%%

{s}+					{return S;}
\/\*[^*]*\*+([^/*][^*]*\*+)*\/ /* ignore comments */
"<!--"					{return CDO;}
"-->"					{return CDC;}
"~="					{return INCLUDES;}
"|="					{return DASHMATCH;}
{w}"{"					{return LBRACE;}
{w}"+"					{return PLUS;}
{w}">"					{return GREATER;}
{w}","					{return COMMA;}
{string}				{return STRING;}
{ident}					{return IDENT;}
"#"{name}				{return HASH;}
"@import"				{return IMPORT_SYM;}
"@page"					{return PAGE_SYM;}
"@media"				{return MEDIA_SYM;}
"@charset"				{return CHARSET_SYM;}
"!"{w}"important"		{return IMPORTANT_SYM;}
{num}em					{return EMS;}
{num}ex					{return EXS;}
{num}px					{return LENGTH;}
{num}cm					{return LENGTH;}
{num}mm					{return LENGTH;}
{num}in					{return LENGTH;}
{num}pt					{return LENGTH;}
{num}pc					{return LENGTH;}
{num}deg				{return ANGLE;}
{num}rad				{return ANGLE;}
{num}grad				{return ANGLE;}
{num}ms					{return TIME;}
{num}s					{return TIME;}
{num}Hz					{return FREQ;}
{num}kHz				{return FREQ;}
{num}{ident}			{return DIMEN;}
{num}%					{return PERCENTAGE;}
{num}					{return NUMBER;}
"url("{w}{string}{w}")"	{return URI;}
"url("{w}{url}{w}")"	{return URI;}
{ident}"("				{return FUNCTION;}
.						{return *yytext;}

