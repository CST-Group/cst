grammar Owrl;

/** The start rule; begin parsing here. */


conf: '['(stat)+ ']' NEWLINE
     |NEWLINE;
     
stat: command '[' (expr)+ ']' ;
    
expr: '[' name atrib* ']' ;
     
atrib: '[' part*  property* ']';

part: '['name part* property*']';

name: '[' object cod ']';

object : '<' ID '>';
 
property : '<' ID '>' qualitydimension+ ;

qualitydimension:'<'ID '>' value;

command : 'create' | 'modify' | 'destroy' ;

value : '<' (INT_VALUE | REAL_VALUE | ('\"' ID* '\"')) '>' ; 

cod: '<'INT_VALUE'>' ;




ID : [a-zA-Z] [a-zA-Z0-9]* ;

INT_VALUE : [0-9]+ ; // match integers

REAL_VALUE : [0-9]+.[0-9]+ ;

NEWLINE:'\r'? '\n' ; // return newlines to parser (is end-statement signal)

WS : [\t]+ -> skip ;// toss out whitespace

