GRAMMER:

E -> T
E -> T|E
T -> F
T -> F*
T -> F+
T -> F?
T -> FT
F -> v
F -> .
F -> \
F -> (E)


VOCABULARY:
All symbols are allowed, except for the following:
*,|,?,+,*,(,),π,µ

µ = branch state in the FSM
π = wild card in the FSM

COMMAND: 
java REcompile "someregex" | REsearch "text.txt" > output.txt