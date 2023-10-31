# Mini Interpretador para Swift

O objetivo desse trabalho é desenvolver um interpretador para um subconjunto de uma linguagem de programação conhecida. Para isso foi criada miniSwift, uma linguagem de programação de brinquedo baseada em Swift (https://www.swift.org). Essa linguagem possui tipagem estática e não possui valores indefinidos (nil).

## Execução
1. para compilar, exeucte na pasta raiz:
   ```bash
   javac -classpath . $(find ./ -type f -name '*.java') 
   ```
2. para exeutar baseado em um arquivo:
   ```bash
   java msi name.msft
   ```
2. para exeutar com instruções no terminal:
   ```bash
   java msi
   ```

## Contextualização

A seguir é dado um exemplo de utilização da linguagem miniSwift. O código a seguir verifica se duas palavras são anagramas.

``` swift
/* Read a word */
print("Entre com uma palavra: ")
let word : String = read()

var ok : Bool = true
while ok {
    /* Build the char map for this word */
    let charmap : Dict<Char,Int> = Dict<Char,Int>()
    for let c : Char in word
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            charmap[c] = charmap.keys().contains(c) ? charmap[c]+1 : 1

    /* Read an anagram of this word */
    print("Entre com um anagrama de '" + word + "': ")
    let anagram : String = read()

    /* Check its properties */
    for let c : Char in anagram
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
            charmap[c] = charmap.keys().contains(c) ? charmap[c]-1 : -1

    /* Build a report */
    var report : String = ""
    for let c : Char in charmap.keys() {
        let n : Int = charmap[c]
        if (n != 0)
            report = report + toString(c) + "(" + toString(n) + ") "
    }

    /* Output the results */
    if (report.empty())
        println("  Okay")
    else {
        println("  Falha: " + report)
        ok = false
    }
}
```

- A linguagem possui comandos para declaração de variáveis (var,let), atribuição (=), impressão (print,println) e depuração (dump) que terminam opcionalmente com ponto vírgula. Também possui blocos de comandos entre par de chaves, comandos condicionais (if com else), comandos de repetição (while,for). Também suporta expressões com operador ternário (?:), operadores conectores (&&,||), operadores relacionais (<,>,<=,>=,==,!=), operadores binários (+,-,*,/), operadores unários (!,-), ações (read,random), conversões explícitas (toBool,toInt,toFloat,toChar,toString), funções (count,empty,keys,values,append,contains) e agrupamentos de expressões entre parênteses.

- A linguagem suporta tipos primitivos e compostos. Os tipos primitivos são: lógico (Bool), inteiro (Int), ponto-flutuante (Float), caractere único entre aspas simples (Char), Texto multi-linhas imutáveis entre aspas duplas (String). Os tipos compostos são: arranjos indexados por inteiros positivos começando com zero que armazenam valores de um único tipo (Array) e dicionário que associam chaves a valores de quaisquer tipos (Dict). Não existe o valor nulo (nil). Tipos primitivos são passados via cópia, enquanto tipos compostos são passados por referência. Strings, arranjos e dicionários são acessados via sintaxe de colchete (string[0], arranjo[1] ou dicionario["one"]). Não se pode acessar índices fora de arranjos e acessos a chaves inexistentes em dicionários. Arranjos (via função append) e dicionários podem crescer dinamicamente quando novos elementos são adicionados a eles, mas seus elementos não podem ser removidos.

- A linguagem possui escopo estático (léxico) para suas variáveis. Elas precisam ser declaradas através das palavras reservadas var ou let antes de seu uso com um tipo associado. Na declaração do tipo let a variável deve ser inicializada na própria declaração e seu valor não pode ser modificado posteriormente, ou seja, a variável é constante. Não se pode atribuir valores de tipos diferentes à essa variável. Não pode haver declaração de variáveis de mesmo nome em um mesmo escopo. Variáveis não inicializadas não podem ser usadas.

- A linguagem não possui conversões implícitas. Todos os operadores esperam operandos de mesmo tipo, exceto os operadores de igualdade (==) e diferença (!=) que operam sobre tipos diferentes. Todas as conversões devem ser feitas de forma explícitas utilizando toBool, toInt, toFloat, toChar ou toString. Por exemplo, para somar um valor do tipo Int com um do tipo Float, deve-se converter explicitamente um deles para realizar a operação: 1 + toInt(1.2) ou toFloat(1) + 1.2. Os operadores aritméticos, exceto a adição funcionam, somente com tipos numéricos (Int e Float). O operador unário de negação (!), ternário (?:), comando condicional (if) e de repetição (while) funcionam somente com tipo lógico (Bool). O comando de repetição for funciona somente com arranjos (Array).

## Características

- A linguagem possui comentários de múltiplas linhas no estilo C, onde qualquer sequência de caracteres entre /* e */ são ignorados. A linguagem possui as seguintes características:

### Comandos

1. **bloco**: zero ou mais comandos entre abre e fecha chaves que formam um escopo.
2. **declaração**: variáveis devem ser declaradas através das palavras-reservadas var ou let; não se pode declarar uma variável de mesmo nome no mesmo escopo; variáveis não declaradas ou não inicializadas não podem ser usadas.
3. **print**/println: imprimir valor da expressão sem/com nova linha.
4. **dump**: imprimir o tipo e o valor com nova linha.
5. **if**: executar comandos se a expressão for verdadeira e executar opcionalmente outros comandos (se houverem) caso contrário.
6. **while**: enquanto a expressão for verdadeira repetir comandos.
7. **for**: repetir comandos para cada caractere de um texto (String) ou elemento de um arranjo (Array).    Variáveis declaradas na assinatura tem escopo dentro do próprio for.
8. **atribuição**: avaliar o valor de uma expressão do lado direito e opcionalmente atribuir à uma expressão do lado esquerdo (se houver).
    
    Ex.: x = i + 1 (avaliação com atribuição).a.append(x) (avaliação sem atribuição).

### Tipos

1. **Lógico (Bool)**: false e true.
2. **Inteiro (Int)**: números inteiros.
3. **Ponto-flutuante (Float)**: números fracionários (ponto-flutuante).
4. **Caractere (Char)**: caractere entre aspas simples.
5. **Texto (String)**: sequência de caracteres entre aspas duplas.
6. **Arranjo (Array<Type>)**: lista de elementos de um mesmo tipo.
7. **Dicionário (Dict<Type,Type>)**: sequência de pares de chave/valor de qualquer tipo.

### Valores

1. Variáveis (começam com _, ou letras, seguidos de _, letras ou dígitos).
2. Literais (lógico, inteiro, ponto-flutuante, caractere e texto).
3. Dinâmicos (arranjo e dicionário).
4. Ações:
    - read: ler uma String do teclado.
    - random: ler um Float aleatório entre 0 e 1.
5. Conversões:
    - toBool: retorna (Bool) falso se false (Bool), 0 (Char ou Int), 0.0 (Float), arranjo (Array) e dicionário (Dict) vazios; retorna verdadeiro (true) caso contrário.
    - toInt: retorna (Int) convertendo o valor de Char, Int e Float para inteiro e 0 caso contrário.
    - toFloat: retorna (Float) convertendo o valor de Char, Int e Float para ponto-flutuante e 0.0 caso contrário.
    - toChar: retorna (Char) convertendo Int e Char para caractere e retorna '\0' caso contrário.
    - toString: retorna (String) para qualquer tipo.
6. Funções
    - count: retorna (Int) quantos caracteres tem uma String ou elementos tem um Array.
    - empty: retorna (Bool) se String, Array ou Dict são vazios.
    - keys: retorna arranjo (Array<Type>) com as chaves de um dicionário (Dict).
    - values: retornar arranjo (Array<Type>) com os valores de um dicionário (Dict).
    - append: adiciona um elemento a um arranjo e retorna o próprio arranjo (Array<Type>).
    - contains: retorna (Bool) se um elemento pertence a um arranjo (Array).

### Operadores

1. Binário:
    - Int, Float e Char: + (adição).
    - String, Array e Dict: + (concatenação).
    - Int, Float: - (subtração), * (multiplicação) e / (divisão).
2. Lógico:
    - Todos os tipos: == (igualdade) e != (diferença).
    - Int, Float,Char e String: < (menor), > (maior), <= (menor igual) e >= (maior igual).
3. Conector
    - Bool: && (E) e || (OU) (ambos usam curto-circuito).
4. Unário:
    - Int e Float: - (inverter sinal).
    - Bool: ! (negação).
5. Ternário: x ? y : z, onde x é Bool e se for verdadeiro avalia em y, caso contrário em z.

## Gramática
A gramática da linguagem miniSwift é dada a seguir no formato de Backus-Naur estendida (EBNF):

``` EBNF
<code>      ::= { <cmd> }
<cmd>       ::= <block> | <decl> | <print> | <dump> | <if> | <while> | <for> | <assign>
<block>     ::= '{' <code> '}'
<decl>      ::= <var> | <let>
<var>       ::= var <name> ':' <type> [ '=' <expr> ] { ',' <name> ':' <type> [ '=' <expr> ] } [';']
<let>       ::= let <name> ':' <type> '=' <expr> { ',' <name> ':' <type> '=' <expr> } [';']
<print>     ::= (print | println) '(' <expr> ')' [';']
<dump>      ::= dump '(' <expr> ')' [';']
<if>        ::= if <expr> <cmd> [ else <cmd> ]
<while>     ::= while <expr> <cmd>
<for>       ::= for ( <name> | ( var | let ) <name> ':' <type> ) in <expr> <cmd>
<assign>    ::= [ <expr> '=' ] <expr> [ ';' ]
<type>      ::= <primitive> | <composed>
<primitive> ::= Bool | Int | Float | Char | String
<composed>  ::= <arraytype> | <dicttype>
<arraytype> ::= Array '<' <type> '>'
<dicttype>  ::= Dict '<' <type> ',' <type> '>'
<expr>      ::= <cond> [ '?' <expr> ':' <expr> ]
<cond>      ::= <rel> { ( '&&' | '||' ) <rel> }
<rel>       ::= <arith> [ ( '<' | '>' | '<=' | '>=' | '==' | '!=' ) <arith> ]
<arith>     ::= <term> { ( '+' | '-' ) <term> }
<term>      ::= <prefix> { ( '*' | '/' ) <prefix> }
<prefix>    ::= [ '!' | '-' ] <factor>
<factor>    ::= ( '(' <expr> ')' | <rvalue> ) <function>
<rvalue>    ::= <const> | <action> | <cast> | <array> | <dict> | <lvalue>
<const>     ::= <bool> | <int> | <float> | <char> | <string>
<bool>      ::= false | true
<action>    ::= ( read | random ) '(' ')'
<cast>      ::= ( toBool | toInt | toFloat | toChar | toString ) '(' <expr> ')'
<array>     ::= <arraytype> '(' [ <expr> { ',' <expr> } ] ')'
<dict>      ::= <dictype> '(' [ <expr> ':' <expr> { ',' <expr> ':' <expr> } ] ')'
<lvalue>    ::= <name> { '[' <expr> ']' }
<function>  ::= { '.' ( <fnoargs> | <fonearg> ) }
<fnoargs>   ::= ( count | empty | keys | values ) '(' ')'
<fonearg>   ::= ( append | contains ) '(' <expr> ')'
```

## Autômato Finito Determinístico

Existem várias estratégias para formação de lexemas. Na implementação desse interpretador será utilizado um autômato finito determinístico, também conhecido como máquina de estados, conforme diagrama a seguir.

<p align="center">
  <img src="images/lexico.jpg?raw=true">
</p>



## Instruções
Deve ser desenvolvido um interpretador em linha de comando que funciona em dois modos. No modo prompt (sem argumentos), onde o interpretador executa os comandos dados pelo usuário em linha de comando até que seja interrompido com Ctrl+D (Cmd+D no macOS).

``` bash
$ ./msi
> let a : Int = 10
> print(a)
10
> ^D
```

Ou no modo de arquivo, onde o interpretador recebe um um programa-fonte na linguagem miniSwift como argumento e executa os comandos especificados por esse programa. Por exemplo, para o programa anagrama.mswift deve-se produzir a seguinte saída:

```bash
$ ./msi anagrama.mswift
Entre com uma palavra: amor
Entre com um anagrama de 'amor': roma
Okay
Entre com um anagrama de 'amor': merengue
Falha: a(1) e(-3) u(-1) g(-1) n(-1) o(1)
```

O programa deverá abortar sua execução, em caso de qualquer erro léxico, sintático ou semântico, indicando uma mensagem de erro. As mensagens são padronizadas indicando o número da linha (2 dígitos) onde ocorreram:

<table border="1" style="margin: 0 auto;">
    <tr>
        <th>Tipo de Erro</th>
        <th>Mensagem</th>
    </tr>
    <tr>
        <td rowspan="2">Léxico</td>
        <td>Lexema inválido [lexema]</td>
    </tr>
    <tr> <td>Fim de arquivo inesperado</td> </tr>
    <tr>
        <td rowspan="2">Sintático</td>
        <td>Lexema não esperado [lexema]</td>
    </tr>
    <tr> <td>Fim de arquivo inesperado</td> </tr>
    <tr>
        <td rowspan="6">Sintático</td>
        <td>Variável não declarada [var]</td>
    </tr>
    <tr> <td>Variável já declarada anteriormente [var]</td> </tr>
    <tr> <td>Variável não inicializada [var]</td> </tr>
    <tr> <td>Atribuição em variável constante [var]</td> </tr>
    <tr> <td>Tipo inválido [tipo]</td> </tr>
    <tr> <td>Fim de arquivo inesperado</td> </tr>
</table>

Exemplo de mensagem de erro:

``` bash
$ ./msi erro.mswift
03: Lexema não esperado [;]
```
