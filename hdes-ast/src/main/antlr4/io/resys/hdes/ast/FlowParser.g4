parser grammar FlowParser;
options { tokenVocab = HdesLexer; }
import CommonParser;

taskTypes
  : MANUAL_TASK
  | FLOW_TASK 
  | DT_TASK
  | ST_TASK;
objectDataType: OBJECT | ARRAY;

flow: id description? inputs tasks EOF;

tasks: 'tasks' ':' '{' taskArgs? '}';
taskArgs: task (',' task)*;
task: endTask | nextTask;
endTask: END ':' '{' mapping '}';
nextTask: typeName ':' '{' (pointer taskRef?)? '}';

pointer: whenThenArgs | then;
whenThenArgs: whenThen (',' whenThen)*;
whenThen: 'when' ':' whenExpression then; 
whenExpression: StringLiteral;
then: 'then' ':' (END | typeName);

taskRef: taskTypes ':' typeName mapping;  
mapping: objectDataType 'mapping' ':' '{' mappingArgs? '}';
mappingArgs: mappingArg (',' mappingArg)*;

mappingArg: typeName ':' mappingValue;
mappingValue: typeName | literal;

