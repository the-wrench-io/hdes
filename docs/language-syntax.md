# HDES language syntax
This document contains description of language syntax for HDES.



# Basic examples

## Decision table

Decision table allows to find output values for given input value combinations with rule set expressions. 

E.g. customer discount may depend of customer age (youth discount, retired person discount), how long person has been customer and how much customer has spent on last year. 
Here is example of such decision table:

```
define decision-table: basic
headers: {
  age INTEGER required IN,
  customerPeriod INTEGER required IN,
  purchaseAmount DECIMAL required IN,
  value DECIMAL required OUT 
} ALL: {
  { between 15 and 18, ?, ?, 25 },
  { >=65, ?, ?, 20 },
  {between 19 and 64, <1, ?, 0},
  {between 19 and 64, between 1 and 3, ?, 10},
  {between 19 and 64, between 4 and 5, ?, 20},
  {between 19 and 64, >5, ?, 30},
  {between 19 and 64, ?, >10000, 10},
}	
```
Table interface is described by `headers`: each entry there contains definition of input or output element. Header entry consists of name, value type (e.g. text, numeric, boolean etc) and direction: IN for input value, which provides base for comparison and OUT for resulting output value.

Table content is defined with hit policy block. Here hit policy `ALL` is used, which means that output contains all matching rule expressions.
Table contains rule sets ("rows"), each ruleset consists of one or several rules ("columns"), which define expressions for rule matching in case of input rule or matching value for output in case of output rule. 

E.g. if we have customer with age 40 years, being customer for 5 years and purchased last year in amount of 15000 then result values for executing this decision table are `20, 10` (5th row for age and customer period match and last row for purchase amount match).

## Flow


## Flow task



# Language details

## Types

### Scalar types
Scalar types are following:
* INTEGER - represents 32-bit signed integer value, for readability purpose numbers can be separated with underscore.
* DECIMAL - arbitrary-precision representation of decimal fractions.
* DATE_TIME
* DATE
* TIME
* STRING - sequence of characters
* BOOLEAN - Boolean type, `true` or `false`

### Complex types
* OBJECT - allows to pass structure, defined as:

```
myobject OBJECT required IN : {attr1:INTEGER required IN, attr2 DECIMAL required IN}
```

* ARRAY - array of types:

```
myarray ARRAY of INTEGER required IN
```

## Expressions
Rules support following expressions:

### Undefined expressions
`?` - this designates undefined expression, meaning that this rule is not used in ruleset.

### relational expressions
This allows to define rule(s) with relationship operators. Possible operators are 
* `=, !=, >=, >, <, <=` for simple relational tests
* `AND, OR` - to combine relational expressions
* `BETEEN x AND y` - for testing against inclusive range

Examples:
* =11
* >=11
* >11 AND <=100
* BETWEEN 11 AND 100

### Matching expressions
This defines literal(s) against which value is compared or output value is set if rule is for output value. Multiple literals can be checked against using `OR` operator, `NOT` can be used to negate result.
Examples:
* 'x'
* 'x' OR 'y'
* NOT 'x' OR 'y'

### Formulas
Formulas can be used to calculate value from output elements. Example usages are:
* calculating sum of values `sum(outputs)`
* calculating average `sum(outputs)/size(outputs)` 

# Decision table elements

## Hit policies
Hit policy defines how output is composed while evaluating rules.
There are 3 types of hit policies:

### All hit policy
`ALL` - In this case output contains values from all matching rule sets.

### First hit policy
`FIRST` - In this case output contains value from first matching rule set. This means that order of rule sets in decision table definition may affect output, if there is more than one matching rule set for given input.


### Matrix hit policy
// TODO
`MATRIX`- In this case table maps possible values and inputs to output value. Example:

```
define decision-table: MatrixDT

headers: {
  name     STRING IN,
  lastName STRING IN,
  value    INTEGER OUT,
  total    INTEGER OUT FORMULA: sum(outputs)/size(outputs) + 10/50

} MATRIX: {
            { 'BOB', 'SAM', ? }
  name:     {  10,    20,   30 },
  lastName: {  20,    50,   60 }
}
```

