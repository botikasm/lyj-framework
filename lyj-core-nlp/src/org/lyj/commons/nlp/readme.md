##Semantic Engine
Internal Semantic Engine is very simple and works on a keywords matching 
using an expression syntax that allow grouping synonymous.
 
 ###Syntax for Expressions
`(WORD1|WORD2|WORD3) [OPERATOR] WORD4 [OPERATOR] WORD5`

Sample:

`(cat|cats|felin|felins) + (like|love) + black - yellow`

In this expression sentences that match are like:
* "I love black cats"
* "I like that cat, it's black!"
* "Yes, I hate all cats that love mice with black eyes."

So, this is not really a smart semantic engine. Last sentence is really 
particular and the engine understand you love black cats.
Otherwise for simple sentences it might be good enough to understand 
what user is asking.

Sentences that does not match are like:
* "I love black and yellow cats"
* "I like that cat, it's yellow!"

This sentences does not match because "yellow" is a banned word (- yellow).

###Operators
Operators are:
* "+" : Add a word to expression
* "-" : Ban a word from an expression. This word cannot be used.
* "|" : Option operator is used to separate words delimited from parenthesis. 
ex: `(cat|cats|felin)`. Use this operator to declare synonymous or plurals.

### Wild char: the Asterisk (*)
 In words you can use the asterisk as a universal character placeholder.
 All this words match with *ats: cats, rats, fats.
 
 Asterisk can be placed at beginning of a word, in middle and at the end.
 * Front Asterisk: **ts
 * Middle Asterisk: c**s
 * End Asterisk: **ts
 
 

