<!-- TOC -->
* [Faul-Lang compiler](#faul-lang-compiler)
  * [Aufbau](#aufbau)
    * [Vision](#vision)
    * [Token](#token)
      * [Basic](#basic)
      * [Keywords](#keywords)
      * [Operatoren](#operatoren)
  * [Todo](#todo)
<!-- TOC -->
# Faul-Lang compiler
Ein kleiner compiler, der faul-lang in mips assembler übersetzt.

## Aufbau
### Vision
Es soll erstmal zwei Datentypen geben, integer (int) und boolean (bool).

Damit soll möglich sein einfach Statements zu formulieren:
```
    int a = 3 + 5;
    int d = 3 * a:
    
    bool b = true;
    bool c = !b
```
Statements werden durch ein Semicolon terminiert.

Block-Statements sind auch geplant. Zunächst soll dies nur `if` beinhalten.

### Token
Tokens der Faul-Lang.
#### Basic

| Name              | Beschreibung             |
|-------------------|:-------------------------|
| EOF               | Ende des Inputs          |
| SEMICOLON         | Statements terminator    |
| V_INT             | Ganzzahl                 |
| V_BOOL            | Boolescher Wahrheitswert |
| OPEN_PARENTHESES  | {                        |
| CLOSE_PARENTHESES | }                        |
| IDENT             | Variablenname            |

#### Keywords

| Name | Beschreibung                 |
|------|------------------------------|
| INT  | Zum erstellen eines Integers |
| BOOL | Zum erstellen eines Boolean  |
| IF   | Beginnt ein If-Statement     |

#### Operatoren

| Name     | Beschreibung |
|:---------|:------------:|
| EQ       |      =       |
| PLUS     |      +       |
| MINUS    |      -       |
| ASTERISK |      *       |
| SLASH    |      /       |
| EQEQ     |      ==      | 
| NOTEQ    |      !=      |
| GT       |      >       |
| GTEQ     |      >=      |
| LT       |      <       |
| LTEQ     |      <=      |
| NOT      |      !       |

## Integer FSM

<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "https://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">

<svg width="800" height="600" version="1.1" xmlns="http://www.w3.org/2000/svg">
	<ellipse stroke="black" stroke-width="1" fill="none" cx="276.5" cy="271.5" rx="30" ry="30"/>
	<text x="267.5" y="277.5" font-family="Times New Roman" font-size="20">s0</text>
	<ellipse stroke="black" stroke-width="1" fill="none" cx="502.5" cy="271.5" rx="30" ry="30"/>
	<text x="493.5" y="277.5" font-family="Times New Roman" font-size="20">s1</text>
	<ellipse stroke="black" stroke-width="1" fill="none" cx="502.5" cy="271.5" rx="24" ry="24"/>
	<path stroke="black" stroke-width="1" fill="none" d="M 263.275,244.703 A 22.5,22.5 0 1 1 289.725,244.703"/>
	<text x="204.5" y="195.5" font-family="Times New Roman" font-size="20">0,1,2,3,4,5,6,7,8,9</text>
	<polygon fill="black" stroke-width="1" points="289.725,244.703 298.473,241.17 290.382,235.292"/>
	<polygon stroke="black" stroke-width="1" points="306.5,271.5 472.5,271.5"/>
	<polygon fill="black" stroke-width="1" points="472.5,271.5 464.5,266.5 464.5,276.5"/>
	<text x="334.5" y="292.5" font-family="Times New Roman" font-size="20">+,-,*,/,&lt;,&gt;,!,);</text>
</svg>


## Todo
- [ ] Lexer
- [ ] Parser
- [ ] Code generation
