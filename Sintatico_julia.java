import java.io.UnsupportedEncodingException;
import javax.swing.filechooser.FileFilter;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import javax.swing.JTextArea;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;

/**
 * Sintático
 * 
 * @author Júlia Koch
 *
 *
 * Gramática:
 * 
 * 
<PROGRAMA> ::= <FUNÇAO> 
<FUNÇAO> ::= 'export' <CONSTANTE_VARIAVEL> <IDENTIFICADOR> '=' '(' <ARGUMENTOS> ')' ':' <TIPO> <SETA> '{' <COMANDOS> '}' ';'
<ARGUMENTOS> ::= <ARGUMENTO> , <ARGUMENTOS>
<ARGUMENTOS> ::= <ARGUMENTO>
<ARGUMENTO> ::= <IDENTIFICADOR> ':' <TIPO>
<TIPO> ::= 'number'
<TIPO> ::= 'string'
<TIPO> ::= 'unknown'
<CONSTANTE_VARIAVEL> ::= 'const' 
<CONSTANTE_VARIAVEL> ::= 'let' 
<DECLARACAO_VAR> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> ':' <TIPO> '=' <VALOR> ";"
<IDENTIFICADOR> ::= [a-zA-Z_][a-zA-Z0-9_]+
<VALOR> ::= <IDENTIFICADOR>
<VALOR> ::= [0-9]+('.'[0-9]+)
<SETA> ::= '=>'
<COMANDOS> ::= <FUNÇAO>
<COMANDOS> ::= <COMANDO>
<COMANDOS> ::= <COMANDO>, <COMANDOS>
<COMANDO> ::= <DECLARACAO_VAR>
<COMANDO> ::= <COMANDO_SE>
<COMANDO> ::= <COMANDO_SENAO>
<COMANDO> ::= <COMANDO_PARA>
<COMANDO> ::= <COMANDO_ENQUANTO>
<COMANDO> ::= <COMANDO_RETURN>
<COMANDO_RETURN> ::= 'return' <E> ';'
<COMANDO_SE> ::= 'if' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'
<COMANDO_SENAO> ::= 'else' '{' <COMANDOS> '}' ';'
<COMANDO_PARA> ::= 'for' '(' <EXPRESSAO> ';' <CONDICAO> ';' <EXPRESSAO> ')' '{' <COMANDOS> '}' ';'
<COMANDO_ENQUANTO> ::= 'while' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'
<EXPRESSAO> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> = <VALOR> ';' <CONDICAO> ';' <EXPRESSAO_FOR> ';'
<EXPRESSAO_FOR> ::= <E> ';'
<EXPRESSAO_FOR> ::= <IDENTIFICADOR> <INCREMENTA_DECREMENTA> ';'
<INCREMENTA_DECREMENTA> ::= '++'
<INCREMENTA_DECREMENTA> ::= '--'
<CONDICAO> ::= <E> '>' <E> 
<CONDICAO> ::= <E> '>=' <E> 
<CONDICAO> ::= <E> '!=' <E> 
<CONDICAO> ::= <E> '<=' <E> 
<CONDICAO> ::= <E> '<' <E> 
<CONDICAO> ::= <E> '==' <E> 
<E> ::= <E> + <T>
<E> ::= <E> - <T>
<E> ::= <T>
<T> ::= <T> * <F>
<T> ::= <T> / <F>
<T> ::= <T> % <F>
<T> ::= <F>
<F> ::= -<F>
<F> ::= <X> ** <F>
<F> ::= <X>
<X> ::= '(' <E> ')'
<X> ::= [0-9]+('.'[0-9]+)
<X> ::= <IDENTIFICADOR>
*
*
*/

public class Sintatico_julia {

	  // Lista de tokens	
  static final int T_PONTO_VIRGULA   =   1;
  static final int T_SE              =   2;
  static final int T_SENAO           =   3;
  static final int T_ENQUANTO        =   4;
  static final int T_PARA            =   5;
  static final int T_LER             =   6;
  static final int T_ABRE_PAR        =   7;
  static final int T_FECHA_PAR       =   9;
  static final int T_MAIOR           =  10;
  static final int T_MENOR           =  11;
  static final int T_MAIOR_IGUAL     =  12;
  static final int T_MENOR_IGUAL     =  13;
  static final int T_IGUAL           =  14;
  static final int T_DIFERENTE       =  15;
  static final int T_MAIS            =  16;
  static final int T_MENOS           =  17;
  static final int T_VEZES           =  18;
  static final int T_DIVIDIDO        =  19;
  static final int T_RESTO           =  20;
  static final int T_ELEVADO         =  21;
  static final int T_EXPORT          =  22;
  static final int T_CONST           =  23;
  static final int T_LET             =  24;
  static final int T_RETORNO          =  25;
  static final int T_TIPO_NUMBER     =  26;
  static final int T_TIPO_STRING     =  27;
  static final int T_TIPO_UNKNOWN    =  28;
  static final int T_INCR            =  29;
  static final int T_DECR            =  30;
  static final int T_SETA            =  31;
  static final int T_NUMERO          =  32;
  static final int T_VIRGULA         =  33;
  static final int T_ESCREVER        =  34;
  static final int T_TIPAGEM         =  35;
  static final int T_ABRE_CHAVE      =  36;
  static final int T_FECHA_CHAVE     =  37;
  static final int T_ID              =  38;
	  
  static final int T_FIM_FONTE       =  90;
  static final int T_ERRO_LEX        =  98;
  static final int T_NULO            =  99;

  static final int FIM_ARQUIVO       = 226;

  static final int E_SEM_ERROS       =   0;
  static final int E_ERRO_LEXICO     =   1;
  static final int E_ERRO_SINTATICO  =   2;

  // Variaveis que surgem no Lexico
  static File arqFonte;
  static BufferedReader rdFonte;
  static File arqDestino;
  static char   lookAhead;
  static int    token;
  static String lexema;
  static int    ponteiro;
  static String linhaFonte;
  static int    linhaAtual;
  static int    colunaAtual;
  static String mensagemDeErro;
  static StringBuffer tokensIdentificados = new StringBuffer();

  // Variaveis adicionadas para o sintatico
  static StringBuffer 	regrasReconhecidas = new StringBuffer();
  static int 			estadoCompilacao;

  public static void main( String s[] ) throws ErroLexicoException
  {
      try {
          abreArquivo();
          abreDestino();
          linhaAtual     = 0;
          colunaAtual    = 0;
          ponteiro       = 0;
          linhaFonte     = "";
          token          = T_NULO;
          mensagemDeErro = "";
          tokensIdentificados.append( "Tokens reconhecidos: \n\n" );
          regrasReconhecidas.append( "\n\nRegras reconhecidas: \n\n" );
          estadoCompilacao 	= E_SEM_ERROS;

          // posiciono no primeiro token
          movelookAhead();
          buscaProximoToken();

          analiseSintatica();

          exibeSaida();

          gravaSaida( arqDestino );

          fechaFonte();

      } catch( FileNotFoundException fnfe ) {
          JOptionPane.showMessageDialog( null, "Arquivo nao existe!", "FileNotFoundException!", JOptionPane.ERROR_MESSAGE );
      } catch( UnsupportedEncodingException uee ) {
          JOptionPane.showMessageDialog( null, "Erro desconhecido", "UnsupportedEncodingException!", JOptionPane.ERROR_MESSAGE );
      } catch( IOException ioe ) {
          JOptionPane.showMessageDialog( null, "Erro de io: " + ioe.getMessage(), "IOException!", JOptionPane.ERROR_MESSAGE );
      } catch( ErroLexicoException ele ) {
          JOptionPane.showMessageDialog( null, ele.getMessage(), "Erro Lexico Exception!", JOptionPane.ERROR_MESSAGE );
      } catch( ErroSintaticoException ese ) {
          JOptionPane.showMessageDialog( null, ese.getMessage(), "Erro Sintatico Exception!", JOptionPane.ERROR_MESSAGE );
      } finally {
          System.out.println( "Execucao terminada!" );
      }
  }

  static void analiseSintatica() throws IOException, ErroLexicoException, ErroSintaticoException {

      funcao();

      if ( estadoCompilacao == E_ERRO_LEXICO ) {
          JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Lexico!", JOptionPane.ERROR_MESSAGE );
      } else if ( estadoCompilacao == E_ERRO_SINTATICO ) {
          JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Sintatico!", JOptionPane.ERROR_MESSAGE );
      } else {
          JOptionPane.showMessageDialog( null, "Analise Sintatica terminada sem erros", "Analise Sintatica terminada!", JOptionPane.INFORMATION_MESSAGE );
		  acumulaRegraSintaticaReconhecida( "<FUNÇAO>" );
      }
  }
  
  //<FUNÇAO> ::= 'export' <CONSTANTE_VARIAVEL> <IDENTIFICADOR> '=' '(' <ARGUMENTOS> ')' ':' <TIPO> <SETA> '{' <COMANDOS> '}' ';'
  private static void funcao() throws IOException, ErroLexicoException, ErroSintaticoException {
	  if ( token == T_EXPORT ) {
		  buscaProximoToken();
		  constVar();
		  id();
          if ( token == T_IGUAL ) {
            buscaProximoToken();
            if ( token == T_ABRE_PAR ) {
                buscaProximoToken();
                args();
                if ( token == T_FECHA_PAR ) {
                    buscaProximoToken();
                    if ( token == T_TIPAGEM ) {
                        buscaProximoToken();
                        tipo();
                        seta();
                        if ( token == T_ABRE_CHAVE ) {
                            buscaProximoToken();
                            cmds();
                            if ( token == T_FECHA_CHAVE ) {
                                buscaProximoToken();
                                if ( token == T_PONTO_VIRGULA ) {
                                    buscaProximoToken();
                                    acumulaRegraSintaticaReconhecida( "<FUNÇAO> ::= 'export' <CONSTANTE_VARIAVEL> <IDENTIFICADOR> '=' '(' <ARGUMENTOS> ')' ':' <TIPO> <SETA> '{' <COMANDOS> '}' ';'" );
                                } else {
                                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n1ponto e virgula esperado, mas encontrei: " + lexema );
                                }
                            } else {
                                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'1}' esperado, mas encontrei: " + lexema );
                            } 
                        } else {
                            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'{' esperado, mas encontrei: " + lexema );
                        }
                    } else {
                        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n':' esperado, mas encontrei: " + lexema );
                    }
                } else {
                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n')' esperado, mas encontrei: " + lexema );
                }
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'(' esperado, mas encontrei: " + lexema );
            }
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'=' esperado, mas encontrei: " + lexema );
        }
      } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'export' esperado, mas encontrei: " + lexema );
      }
  }

  //<ARGUMENTOS> ::= <ARGUMENTO> , <ARGUMENTOS>
  private static void args() throws IOException, ErroLexicoException, ErroSintaticoException {
    arg();
    while ( token == T_VIRGULA ) {
        buscaProximoToken();
        args();
    }
    acumulaRegraSintaticaReconhecida( "<ARGUMENTOS> ::= <ARGUMENTO>, <ARGUMENTO> | <ARGUMENTOS>" );
  }

  //<ARGUMENTO> ::= <IDENTIFICADOR> ':' <TIPO>
  private static void arg() throws IOException, ErroLexicoException, ErroSintaticoException {
    id();
    if ( token == T_TIPAGEM ) {
        buscaProximoToken();
        tipo();
        acumulaRegraSintaticaReconhecida( "<ARGUMENTO> ::= <IDENTIFICADOR> ':' <TIPO>" );
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n':' esperado, mas encontrei: " + lexema );
    } 
  }

  //<TIPO> ::= 'number'
  //<TIPO> ::= 'string'
  //<TIPO> ::= 'unknown'
  private static void tipo() throws IOException, ErroLexicoException, ErroSintaticoException {
    if ( token == T_TIPO_NUMBER ) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<TIPO> ::= 'number'" );
    } else if (token == T_TIPO_STRING) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<TIPO> ::= 'string'" );
    } else if (token == T_TIPO_UNKNOWN) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<TIPO> ::= 'unknown'" );
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\ntipo esperado, mas encontrei: " + lexema );
    } 
  }

  //<CONSTANTE_VARIAVEL> ::= 'const' 
  //<CONSTANTE_VARIAVEL> ::= 'let' 
  private static void constVar() throws IOException, ErroLexicoException, ErroSintaticoException {
    if ( token == T_CONST ) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<CONSTANTE_VARIAVEL> ::= 'const'" );
    } else if (token == T_LET) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<CONSTANTE_VARIAVEL> ::= 'let' " );
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'let' ou 'const' esperado, mas encontrei: " + lexema );
    } 
  }

 //<DECLARACAO_VAR> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> ':' <TIPO> '=' <VALOR> ";"
    private static void var() throws IOException, ErroLexicoException, ErroSintaticoException {
        constVar();
        id();
        if ( token == T_TIPAGEM ) {
            buscaProximoToken();
            tipo();
            if ( token == T_IGUAL ) {
            buscaProximoToken();
            valor();
            if ( token == T_PONTO_VIRGULA ) {
                acumulaRegraSintaticaReconhecida( "<DECLARACAO_VAR> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> ':' <TIPO> '=' <VALOR>" );
            }  else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nponto e virgula esperado, mas encontrei: " + lexema );
            } 
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'=' esperado, mas encontrei: " + lexema );
            } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n':' esperado, mas encontrei: " + lexema );
        } 
    }

  //<IDENTIFICADOR> ::= [a-zA-Z_][a-zA-Z0-9_]+
  private static void id() throws IOException, ErroLexicoException, ErroSintaticoException {
	if ( token == T_ID ) {
		buscaProximoToken();
		acumulaRegraSintaticaReconhecida( "<IDENTIFICADOR> ::= [a-zA-Z_][a-zA-Z0-9_]+" );
	} else {
		registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um identificador. Encontrei: " + lexema );
	}
  }
   
  //<VALOR> ::= <IDENTIFICADOR>
  //<VALOR> ::= [0-9]+('.'[0-9]+)
  private static void valor() throws IOException, ErroLexicoException, ErroSintaticoException {
    if ( token == T_ID ) {
        buscaProximoToken();
        id();
        acumulaRegraSintaticaReconhecida( "<VALOR> ::= <IDENTIFICADOR>" );
    } else if (token == T_NUMERO) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<VALOR> ::= [0-9]+('.'[0-9]+)" );
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nidentificador ou número esperado, mas encontrei: " + lexema );
    } 
 }

  //<SETA> ::= '=>'
  private static void seta() throws IOException, ErroLexicoException, ErroSintaticoException {
    if ( token == T_SETA ) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<SETA> ::= '=>'" );
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'=>' esperado, mas encontrei: " + lexema );
    } 
  }

 // <COMANDOS> ::= <FUNÇAO>
 // <COMANDOS> ::= <COMANDO>
 // <COMANDOS> ::= <COMANDO>, <COMANDOS>
 private static void cmds() throws IOException, ErroLexicoException, ErroSintaticoException {
    if (token == T_EXPORT) {
        funcao();  
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida("<COMANDOS> ::= <FUNÇÃO>");
    } else {
        cmd();
        buscaProximoToken();
        
        if(token == T_SENAO || token == T_SE || token == T_ENQUANTO || token == T_PARA || token == T_RETORNO) {
            cmd();
            buscaProximoToken();
        }

        acumulaRegraSintaticaReconhecida("<COMANDOS> ::= <COMANDO> ; <COMANDOS> | <COMANDO>");
    }
}

  //<COMANDO> ::= <DECLARACAO_VAR>
  //<COMANDO> ::= <COMANDO_SE>
  //<COMANDO> ::= <COMANDO_SENAO>
  //<COMANDO> ::= <COMANDO_PARA>
  //<COMANDO> ::= <COMANDO_ENQUANTO>
  //<COMANDO> ::= <COMANDO_RETURN>
  private static void cmd() throws IOException, ErroLexicoException, ErroSintaticoException {
    switch ( token ) {
    case T_CONST: var(); break;
    case T_LET: var(); break;
    case T_SE: cmd_se(); break;
    case T_SENAO: cmd_senao(); break;
    case T_PARA: cmd_para(); break;
    case T_ENQUANTO: cmd_enquanto(); break;
    case T_RETORNO: cmd_retorno(); break;
   
    default:
        registraErroSintatico( "Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nComando nao identificado, encontrei: " + lexema );
    }
    acumulaRegraSintaticaReconhecida( "<COMANDO> ::= <DECLARACAO_VAR>|<COMANDO_SE>|<COMANDO_SENAO>|<COMANDO_PARA>|<COMANDO_ENQUANTO>|<COMANDO_RETURN>" );
 }

 //<COMANDO_RETURN> ::= 'return' <E> ';'
 private static void cmd_retorno() throws IOException, ErroLexicoException, ErroSintaticoException {
    if (token == T_RETORNO) {
        buscaProximoToken();  
        e();
        buscaProximoToken();  

        if (token == T_PONTO_VIRGULA) {
            acumulaRegraSintaticaReconhecida("<COMANDO_RETURN> ::= 'return' <E> ';'");
        } else {
            registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nponto e vírgula esperado, mas encontrei: " + lexema);
        }
    } else {
        registraErroSintatico("Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'return' esperado, mas encontrei: " + lexema);
    }
}

 //<COMANDO_SE> ::= 'if' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'
 private static void cmd_se() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_SE) {
        buscaProximoToken();
        if( token == T_ABRE_PAR) {
            buscaProximoToken();
            condicao();
            if( token == T_FECHA_PAR) {
                buscaProximoToken();
                if( token == T_ABRE_CHAVE) {
                    buscaProximoToken();
                    cmds();
                    if( token == T_FECHA_CHAVE) {
                        buscaProximoToken();
                        if( token == T_PONTO_VIRGULA) {
                            acumulaRegraSintaticaReconhecida( "<COMANDO_SE> ::= 'if' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'" );
                        }  else {
                            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nponto e vírgula esperado, mas encontrei: " + lexema );
                        } 
                    }  else {
                        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'2}' esperado, mas encontrei: " + lexema );
                    } 
                }  else {
                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'{' esperado, mas encontrei: " + lexema );
                } 
            }  else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n')' esperado, mas encontrei: " + lexema );
            } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'(' esperado, mas encontrei: " + lexema );
        } 
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'if' esperado, mas encontrei: " + lexema );
    } 
 }


 //<COMANDO_SENAO> ::= 'else' '{' <COMANDOS> '}' ';'
 private static void cmd_senao() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_SENAO) {
        buscaProximoToken();
        if( token == T_ABRE_CHAVE) {
            buscaProximoToken();
            cmds();
            if( token == T_FECHA_CHAVE) {
                buscaProximoToken();
                if( token == T_PONTO_VIRGULA) {
                    acumulaRegraSintaticaReconhecida( "<COMANDO_SENAO> ::= 'else' '{' <COMANDOS> '}' ';'" );
                } else {
                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n2ponto e virgula esperado, mas encontrei: " + lexema );
                } 
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n} esperado, mas encontrei: " + lexema );
            } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n{ esperado, mas encontrei: " + lexema );
        } 
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nsenao esperado, mas encontrei: " + lexema );
    } 
 }

 //<COMANDO_PARA> ::= 'for' '(' <EXPRESSAO> <CONDICAO> ';' <EXPRESSAO> ')' '{' <COMANDOS> '}' ';'
 private static void cmd_para() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_PARA) {
        buscaProximoToken();
        if( token == T_ABRE_PAR) {
            buscaProximoToken();
            expressao();
            if( token == T_FECHA_PAR) {
                buscaProximoToken();
                if( token == T_ABRE_CHAVE) {
                    buscaProximoToken();
                    cmds();
                    if( token == T_FECHA_CHAVE) {
                        buscaProximoToken();
                        if( token == T_PONTO_VIRGULA) {
                            acumulaRegraSintaticaReconhecida( "<COMANDO_PARA> ::= 'for' '(' <EXPRESSAO> ';' <CONDICAO> ';' <EXPRESSAO> ')' '{' <COMANDOS> '}' ';'" );
                        } else {
                            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n3ponto e virgula esperado, mas encontrei: " + lexema );
                        } 
                    } else {
                        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n} esperado, mas encontrei: " + lexema );
                    }  
                } else {
                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n} esperado, mas encontrei: " + lexema );
                    }
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n) esperado, mas encontrei: " + lexema );
                } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n( esperado, mas encontrei: " + lexema );
        } 
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'for' esperado, mas encontrei: " + lexema );
    } 
 }

 //<COMANDO_ENQUANTO> ::= 'while' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'
 private static void cmd_enquanto() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_ENQUANTO) {
        buscaProximoToken();
        if( token == T_ABRE_PAR) {
            buscaProximoToken();
            condicao();
            if( token == T_FECHA_PAR) {
                buscaProximoToken();
                if( token == T_ABRE_CHAVE) {
                    buscaProximoToken();
                    cmds();
                    if( token == T_FECHA_CHAVE) {
                        buscaProximoToken();
                        if( token == T_PONTO_VIRGULA) {
                            acumulaRegraSintaticaReconhecida( "<COMANDO_ENQUANTO> ::= 'while' '(' <CONDICAO> ')' '{' <COMANDOS> '}' ';'" );
                        } else {
                            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nponto e virgula esperado, mas encontrei: " + lexema );
                        } 
                    } else {
                        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'}' esperado, mas encontrei: " + lexema );
                    } 
                } else {
                    registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'{' esperado, mas encontrei: " + lexema );
                } 
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n')' esperado, mas encontrei: " + lexema );
            } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'(' esperado, mas encontrei: " + lexema );
        } 
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'while' esperado, mas encontrei: " + lexema );
    } 
 }

 //<EXPRESSAO> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> = <VALOR> ';' <CONDICAO> ';' <EXPRESSAO_FOR> ';'
 private static void expressao() throws IOException, ErroLexicoException, ErroSintaticoException {
    constVar();
    id();
    if( token == T_IGUAL) {
        buscaProximoToken();
        valor();
        if( token == T_PONTO_VIRGULA) {
            buscaProximoToken();
            condicao();
            if( token == T_PONTO_VIRGULA) {
                buscaProximoToken();
                expressao_for();
                acumulaRegraSintaticaReconhecida( "<EXPRESSAO> ::= <CONSTANTE_VARIAVEL> <IDENTIFICADOR> = <VALOR> ';' <CONDICAO> ';' <EXPRESSAO_FOR> ';'" );
            } else {
                registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n6ponto e virgula esperado, mas encontrei: " + lexema );
            } 
        } else {
            registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n2ponto e virgula esperado, mas encontrei: " + lexema );
        } 
    } else {
        registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\n'igual' esperado, mas encontrei: " + lexema );
    } 
 }

 //<EXPRESSAO_FOR> ::= <E> ';'
 //<EXPRESSAO_FOR> ::= <IDENTIFICADOR> <INCREMENTA_DECREMENTA> ';'
 private static void expressao_for() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_ID) {
        buscaProximoToken();
        inc_dec();
        if( token == T_PONTO_VIRGULA ){
            buscaProximoToken();
            acumulaRegraSintaticaReconhecida( "<EXPRESSAO_FOR> ::= <IDENTIFICADOR> <INCREMENTA_DECREMENTA> ';'" );
        }
    } else {
        e();
        buscaProximoToken();
        if(token == T_PONTO_VIRGULA){
        acumulaRegraSintaticaReconhecida( "<EXPRESSAO_FOR> ::= <E> ';'" );
        }
    }
 }

 //<INCREMENTA_DECREMENTA> ::= '++'
 //<INCREMENTA_DECREMENTA> ::= '--'
 private static void inc_dec() throws IOException, ErroLexicoException, ErroSintaticoException {
    if( token == T_INCR) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<INCREMENTA_DECREMENTA> ::= '++'" );
    } 
    if (token == T_DECR) {
        buscaProximoToken();
        acumulaRegraSintaticaReconhecida( "<INCREMENTA_DECREMENTA> ::= '--'" );
    }  
 }

 // <CONDICAO> ::= <E> '>' <E> 
  // <CONDICAO> ::= <E> '>=' <E> 
  // <CONDICAO> ::= <E> '!=' <E> 
  // <CONDICAO> ::= <E> '<=' <E> 
  // <CONDICAO> ::= <E> '<' <E> 
  // <CONDICAO> ::= <E> '==' <E>
  private static void condicao() throws ErroLexicoException, IOException, ErroSintaticoException {
    e();
    switch ( token ) {
    case T_MAIOR: buscaProximoToken(); e(); break; 
    case T_MENOR: buscaProximoToken(); e(); break; 
    case T_MAIOR_IGUAL: buscaProximoToken(); e(); break; 
    case T_MENOR_IGUAL: buscaProximoToken(); e(); break; 
    case T_IGUAL: buscaProximoToken(); e(); break; 
    case T_DIFERENTE: buscaProximoToken(); e(); break;
    default: registraErroSintatico( "Erro Sintatico. Linha: " + linhaAtual + "\nColuna: " + colunaAtual + "\nErro: <" + linhaFonte + ">\nEsperava um operador logico. Encontrei: " + lexema );
    }
    acumulaRegraSintaticaReconhecida( "<CONDICAO> ::= <E> ('>'|'>='|'!='|'<='|'<'|'==') <E> " );
 }

  // <E> ::= <E> + <T>
  // <E> ::= <E> - <T>
  // <E> ::= <T>
  private static void e() throws IOException, ErroLexicoException, ErroSintaticoException {
    t(); 
    buscaProximoToken();

    if (token != T_MAIS && token != T_MENOS) {
        return;
    }

    while ((token == T_MAIS) || (token == T_MENOS)) {
        buscaProximoToken();
        t();
    }

    acumulaRegraSintaticaReconhecida("<E> ::= <E> + <T>|<E> - <T>|<T>");
}

  // <T> ::= <T> * <F>
  // <T> ::= <T> / <F>
  // <T> ::= <T> % <F>
  // <T> ::= <F>
  private static void t() throws IOException, ErroLexicoException, ErroSintaticoException {
    f();
    while ( (token == T_VEZES) || (token == T_DIVIDIDO) || (token == T_RESTO) ) {
        buscaProximoToken();
        f();
    }
    acumulaRegraSintaticaReconhecida( "<T> ::= <T> * <F>|<T> / <F>|<T> % <F>|<F>" );
 }

  // <F> ::= -<F>
  // <F> ::= <X> ** <F>
  // <F> ::= <X>     
  private static void f() throws IOException, ErroLexicoException, ErroSintaticoException {
    if ( token == T_MENOS ) {
        buscaProximoToken();
        f();
    } else {
        x();
        while ( token == T_ELEVADO ) {
            buscaProximoToken();
            x();
        }
    }
    acumulaRegraSintaticaReconhecida( "<F> ::= -<F>|<X> ** <F>|<X> " );
    
 }

  // <X> ::= '(' <E> ')'
  // <X> ::= [0-9]+('.'[0-9]+)
  // <X> ::= <IDENTIFICADOR>
  private static void x() throws IOException, ErroLexicoException, ErroSintaticoException {
    switch ( token ) {
        case T_ID: acumulaRegraSintaticaReconhecida( "<X> ::= <IDENTIFICADOR>" ); break;
        case T_NUMERO: acumulaRegraSintaticaReconhecida( "<X> ::= [0-9]+('.'[0-9]+)" ); break;
        case T_ABRE_PAR: {
            buscaProximoToken(); 
            e();
            if ( token == T_FECHA_PAR ) {
                buscaProximoToken();
                acumulaRegraSintaticaReconhecida( "<X> ::= '(' <E> ')'" );
            } else {
                registraErroSintatico( "Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\n')' esperado mas encontrei: " + lexema );
            }
        } break;
     default: registraErroSintatico( "Erro Sintatico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nFator invalido: encontrei: " + lexema );   
    }
 }

 static void fechaFonte() throws IOException
  {
      rdFonte.close();
  }

  static void movelookAhead() throws IOException
  {
	  
    if ( ( ponteiro + 1 ) > linhaFonte.length() ) {

    	linhaAtual++;
        ponteiro = 0;
        
        
        if ( ( linhaFonte = rdFonte.readLine() ) == null ) {
            lookAhead = FIM_ARQUIVO;
        } else {

        	StringBuffer sbLinhaFonte = new StringBuffer( linhaFonte );
        	sbLinhaFonte.append( '\13' ).append( '\10' );
        	linhaFonte = sbLinhaFonte.toString();
        	
            lookAhead = linhaFonte.charAt( ponteiro );
        }
    } else {
        lookAhead = linhaFonte.charAt( ponteiro );
    }

    ponteiro++;
    colunaAtual = ponteiro + 1;
  }

  static void buscaProximoToken() throws IOException, ErroLexicoException
  {
	//int i, j;
        
    StringBuffer sbLexema = new StringBuffer( "" );

    // Salto espaçoes enters e tabs até o inicio do proximo token
  	while ( ( lookAhead == 9 ) ||
	        ( lookAhead == '\n' ) ||
	        ( lookAhead == 8 ) ||
	        ( lookAhead == 11 ) ||
	        ( lookAhead == 12 ) ||
	        ( lookAhead == '\r' ) ||
	        ( lookAhead == 32 ) )
    {
        movelookAhead();
    }

      /*--------------------------------------------------------------*
     * Caso o primeiro caracter seja alfabetico, procuro capturar a *
     * sequencia de caracteres que se segue a ele e classifica-la   *
     *--------------------------------------------------------------*/
    if ( ( lookAhead >= 'a' ) && ( lookAhead <= 'z' ) ) {   
        sbLexema.append( lookAhead );
        movelookAhead();

        while ( ( ( lookAhead >= 'a' ) && ( lookAhead <= 'z' ) ) ||
        		( ( lookAhead >= '0' ) && ( lookAhead <= '9' ) ) || ( lookAhead == '_' ) )
        {
            sbLexema.append( lookAhead );
            movelookAhead();
        }

        lexema = sbLexema.toString();  

        /* Classifico o meu token como palavra reservada ou id */
        if ( lexema.equals( "export" ) )
            token = T_EXPORT;
        else if ( lexema.equals( "const" ) )
            token = T_CONST;
        else if ( lexema.equals( "let" ) )
            token = T_LET;
        else if ( lexema.equals( "return" ) )
            token = T_RETORNO;
        else if ( lexema.equals( "number" ) )
            token = T_TIPO_NUMBER;
        else if ( lexema.equals( "string" ) )
            token = T_TIPO_STRING;
        else if ( lexema.equals( "unknown" ) )
            token = T_TIPO_UNKNOWN;
        else if ( lexema.equals( "if" ) )
            token = T_SE;
        else if ( lexema.equals( "else" ) )
            token = T_SENAO;
        else if ( lexema.equals( "while" ) )
            token = T_ENQUANTO;
        else if ( lexema.equals( "for" ) )
            token = T_PARA;
        else {
            token = T_ID;
        }
    } else if ( ( lookAhead >= '0' ) && ( lookAhead <= '9' ) ) {
        sbLexema.append( lookAhead );
        movelookAhead();
        while ( ( lookAhead >= '0' ) && ( lookAhead <= '9' ) )
        {
            sbLexema.append( lookAhead );
            movelookAhead();
        }
        token = T_NUMERO;    	
    } else if ( lookAhead == '{' ){
        sbLexema.append( lookAhead );
        token = T_ABRE_CHAVE;    	
        movelookAhead();
    } else if ( lookAhead == '}' ){
        sbLexema.append( lookAhead );
        token = T_FECHA_CHAVE;    	
        movelookAhead();
    } else if ( lookAhead == '(' ){
        sbLexema.append( lookAhead );
        token = T_ABRE_PAR;    	
        movelookAhead();
    } else if ( lookAhead == ')' ){
        sbLexema.append( lookAhead );
        token = T_FECHA_PAR;    	
        movelookAhead();
    }
    else if ( lookAhead == '=' ){
        sbLexema.append( lookAhead ); 	
        movelookAhead();
        if (lookAhead == '>') {
            sbLexema.append(lookAhead);
            token = T_SETA; 
            movelookAhead();
        } else {
            token = T_IGUAL;  
        }
    } else if ( lookAhead == ':' ){
        sbLexema.append( lookAhead );
        token = T_TIPAGEM;    	
        movelookAhead();
    } 
    else if ( lookAhead == ';' ){
        sbLexema.append( lookAhead );
        token = T_PONTO_VIRGULA;    	
        movelookAhead();
    } else if ( lookAhead == ',' ){
        sbLexema.append( lookAhead );
        token = T_VIRGULA;    	
        movelookAhead();
    } else if (lookAhead == '+') {
        sbLexema.append(lookAhead);
        movelookAhead();
        if (lookAhead == '+') {
            sbLexema.append(lookAhead);
            token = T_INCR; 
            movelookAhead();
        } else {
            token = T_MAIS;  
        }
    } else if (lookAhead == '-') {
        sbLexema.append(lookAhead);
        movelookAhead();
        if (lookAhead == '-') {
            sbLexema.append(lookAhead);
            token = T_DECR;  
            movelookAhead();
        } else {
            token = T_MENOS;  
        }
    } else if ( lookAhead == '*' ){
        sbLexema.append( lookAhead );
        movelookAhead();
        if ( lookAhead == '*' ) {
            sbLexema.append( lookAhead );
            movelookAhead();
            token = T_ELEVADO;    	
        } else {
            token = T_VEZES;    	
        }
    } else if ( lookAhead == '/' ){
        sbLexema.append( lookAhead );
        token = T_DIVIDIDO;    	
        movelookAhead();
    } else if ( lookAhead == '%' ){
        sbLexema.append( lookAhead );
        token = T_RESTO;    	
        movelookAhead();
    } else if ( lookAhead == '<' ){
        sbLexema.append( lookAhead );
        movelookAhead();
        if ( lookAhead == '>' ) {
            sbLexema.append( lookAhead );
            movelookAhead();
            token = T_DIFERENTE;
        } else if ( lookAhead == '=' ) {  
            sbLexema.append( lookAhead );
            movelookAhead();
            token = T_MENOR_IGUAL;
        } else {
            token = T_MENOR;    	
        }
    } else if ( lookAhead == '>' ){
        sbLexema.append( lookAhead );
        movelookAhead();
        if ( lookAhead == '=' ) {
            sbLexema.append( lookAhead );
            movelookAhead();
            token = T_MAIOR_IGUAL;
        } else {
            token = T_MAIOR;    	
        }        
    } else if ( lookAhead == FIM_ARQUIVO ){
         token = T_FIM_FONTE;    	
    } else {
    	token = T_ERRO_LEX;
    	mensagemDeErro = "Erro Léxico na linha: " + linhaAtual + "\nReconhecido ao atingir a coluna: " + colunaAtual + "\nLinha do Erro: <" + linhaFonte + ">\nToken desconhecido: " + lookAhead;
    	sbLexema.append( lookAhead );
    }
        
    lexema = sbLexema.toString();  
  }
  
  static void mostraToken()
  {

    StringBuffer tokenLexema = new StringBuffer( "" );
    switch ( token ) {
    case T_PONTO_VIRGULA    : tokenLexema.append( "T_PONTO_VIRGULA" ); break;
    case T_SE    : tokenLexema.append( "T_SE" ); break;
    case T_SENAO    : tokenLexema.append( "T_SENAO" ); break;
    case T_ENQUANTO    : tokenLexema.append( "T_ENQUANTO" ); break;
    case T_PARA            : tokenLexema.append( "T_PARA" ); break;
    case T_LER             : tokenLexema.append( "T_LER" ); break;
    case T_ABRE_PAR        : tokenLexema.append( "T_ABRE_PAR" ); break;
    case T_FECHA_PAR       : tokenLexema.append( "T_FECHA_PAR" ); break;
    case T_ESCREVER        : tokenLexema.append( "T_ESCREVER" ); break;
    case T_MAIOR           : tokenLexema.append( "T_MAIOR" ); break;
    case T_MENOR           : tokenLexema.append( "T_MENOR" ); break;
    case T_MAIOR_IGUAL     : tokenLexema.append( "T_MAIOR_IGUAL" ); break;
    case T_MENOR_IGUAL     : tokenLexema.append( "T_MENOR_IGUAL" ); break;
    case T_IGUAL           : tokenLexema.append( "T_IGUAL" ); break;
    case T_DIFERENTE       : tokenLexema.append( "T_DIFERENTE" ); break;
    case T_MAIS            : tokenLexema.append( "T_MAIS" ); break;
    case T_MENOS           : tokenLexema.append( "T_MENOS" ); break;
    case T_VEZES           : tokenLexema.append( "T_VEZES" ); break;
    case T_DIVIDIDO        : tokenLexema.append( "T_DIVIDIDO" ); break;
    case T_RESTO           : tokenLexema.append( "T_RESTO" ); break;
    case T_ELEVADO         : tokenLexema.append( "T_ELEVADO" ); break;
    case T_EXPORT         : tokenLexema.append( "T_EXPORT" ); break;
    case T_CONST         : tokenLexema.append( "T_CONST" ); break;
    case T_LET         : tokenLexema.append( "T_LET" ); break;
    case T_RETORNO         : tokenLexema.append( "T_RETORNO" ); break;
    case T_TIPO_NUMBER         : tokenLexema.append( "T_TIPO_NUMBER" ); break;
    case T_TIPO_STRING         : tokenLexema.append( "T_TIPO_STRING" ); break;
    case T_TIPO_UNKNOWN         : tokenLexema.append( "T_TIPO_UNKNOWN" ); break;
    case T_INCR         : tokenLexema.append( "T_INCR" ); break;
    case T_DECR         : tokenLexema.append( "T_DECR" ); break;
    case T_SETA            : tokenLexema.append( "T_SETA" ); break;
    case T_NUMERO          : tokenLexema.append( "T_NUMERO" ); break;
    case T_VIRGULA    : tokenLexema.append( "T_VIRGULA" ); break;
    case T_ID              : tokenLexema.append( "T_ID" ); break;
    case T_FIM_FONTE       : tokenLexema.append( "T_FIM_FONTE" ); break;
    case T_ERRO_LEX        : tokenLexema.append( "T_ERRO_LEX" ); break;
    case T_NULO            : tokenLexema.append( "T_NULO" ); break;
    case T_TIPAGEM           : tokenLexema.append( "T_TIPAGEM" ); break;
    case T_ABRE_CHAVE           : tokenLexema.append( "T_ABRE_CHAVE" ); break;
    case T_FECHA_CHAVE           : tokenLexema.append( "T_FECHA_CHAVE" ); break;
    default                : tokenLexema.append( "N/A" ); break;
    }
    System.out.println( tokenLexema.toString() + " ( " + lexema + " )" );
    acumulaToken( tokenLexema.toString() + " ( " + lexema + " )" );
    tokenLexema.append( lexema );
  }

  private static void abreArquivo() {

    JFileChooser fileChooser = new JFileChooser();
    
    fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );

    FiltroSab filtro = new FiltroSab();
    
    fileChooser.addChoosableFileFilter( filtro );
    int result = fileChooser.showOpenDialog( null );
    
    if( result == JFileChooser.CANCEL_OPTION ) {
        return;
    }
    
    arqFonte = fileChooser.getSelectedFile();
    abreFonte( arqFonte ); 	

 }


	private static boolean abreFonte( File fileName ) {

		if( arqFonte == null || fileName.getName().trim().equals( "" ) ) {
			JOptionPane.showMessageDialog( null, "Nome de Arquivo Inv�lido", "Nome de Arquivo Inv�lido", JOptionPane.ERROR_MESSAGE );
			return false;
		} else {
			linhaAtual = 1;
	        try {
				FileReader fr = new FileReader( arqFonte );
				rdFonte = new BufferedReader( fr );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return true;
		}
	}

	private static void abreDestino() {

		JFileChooser fileChooser = new JFileChooser();
			
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );

		FiltroSab filtro = new FiltroSab();
		    
		fileChooser.addChoosableFileFilter( filtro );
		int result = fileChooser.showSaveDialog( null );
			
		if( result == JFileChooser.CANCEL_OPTION ) {
			return;
		}
			
		arqDestino = fileChooser.getSelectedFile();
	}	
	

	private static boolean gravaSaida( File fileName ) {

		if( arqDestino == null || fileName.getName().trim().equals( "" ) ) {
			JOptionPane.showMessageDialog( null, "Nome de Arquivo Inv�lido", "Nome de Arquivo Inv�lido", JOptionPane.ERROR_MESSAGE );
			return false;
		} else {
			FileWriter fw;
			try {
				System.out.println( arqDestino.toString() );
				System.out.println( tokensIdentificados.toString() );
				fw = new FileWriter( arqDestino );
				BufferedWriter bfw = new BufferedWriter( fw ); 
				bfw.write( tokensIdentificados.toString() );
				bfw.close();
				JOptionPane.showMessageDialog( null, "Arquivo Salvo: " + arqDestino, "Salvando Arquivo", JOptionPane.INFORMATION_MESSAGE );
			} catch (IOException e) {
				JOptionPane.showMessageDialog( null, e.getMessage(), "Erro de Entrada/Sa�da", JOptionPane.ERROR_MESSAGE );
			} 
			return true;
		}
	}

    public static void exibeTokens() {
		
		JTextArea texto = new JTextArea();
		texto.append( tokensIdentificados.toString() );
		JOptionPane.showMessageDialog(null, texto, "Tokens Identificados (token/lexema)", JOptionPane.INFORMATION_MESSAGE );
	}

    public static void acumulaRegraSintaticaReconhecida( String regra ) {

		regrasReconhecidas.append( regra );
		regrasReconhecidas.append( "\n" );
		
	}
	
	public static void acumulaToken( String tokenIdentificado ) {

		tokensIdentificados.append( tokenIdentificado );
		tokensIdentificados.append( "\n" );
		
	}

 public static void exibeSaida() {

    JTextArea texto = new JTextArea();
    texto.append( tokensIdentificados.toString() );
    JOptionPane.showMessageDialog(null, texto, "Analise Lexica", JOptionPane.INFORMATION_MESSAGE );

    texto.setText( regrasReconhecidas.toString() );
    texto.append( "\n\nStatus da Compilacao:\n\n" );
    texto.append( mensagemDeErro );

    JOptionPane.showMessageDialog(null, texto, "Resumo da Compilacao", JOptionPane.INFORMATION_MESSAGE );
 }

 static void registraErroSintatico( String msg ) throws ErroSintaticoException {
    if ( estadoCompilacao == E_SEM_ERROS ) {
        estadoCompilacao = E_ERRO_SINTATICO;
        mensagemDeErro = msg;
    }
    throw new ErroSintaticoException( msg ); 
 }

}   

/**
 * Classe Interna para criacao de filtro de selecao
 */
class FiltroSab extends FileFilter {

	public boolean accept(File arg0) {
	   	 if(arg0 != null) {
	         if(arg0.isDirectory()) {
	       	  return true;
	         }
	         if( getExtensao(arg0) != null) {
	        	 if ( getExtensao(arg0).equalsIgnoreCase( "grm" ) ) {
		        	 return true;
	        	 }
	         };
	   	 }
	     return false;
	}

	/**
	 * Retorna quais extensoes poderao ser escolhidas
	 */
	public String getDescription() {
		return "*.grm";
	}
	
	/**
	 * Retorna a parte com a extensao de um arquivo
	 */
	public String getExtensao(File arq) {
	if(arq != null) {
		String filename = arq.getName();
	    int i = filename.lastIndexOf('.');
	    if(i>0 && i<filename.length()-1) {
	    	return filename.substring(i+1).toLowerCase();
	    };
	}
		return null;
	}
}
