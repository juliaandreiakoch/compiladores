import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

/**
 * Lexico
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
<COMANDOS> ::= <COMANDO>, <COMANDOS>
<COMANDOS> ::= <COMANDO> 
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
<EXPRESSAO_FOR> ::= <IDENTIFICADOR> <INCREMENTA_DECREMENTA> 
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

public class Lexico_julia {

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
  static final int T_RETORNO         =  25;
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

  static final int FIM_ARQUIVO       =  26;

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

  public static void main( String s[] ) throws java.io.IOException
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

          movelookAhead();

          while ( ( token != T_FIM_FONTE ) && ( token != T_ERRO_LEX ) ) {
                  buscaProximoToken();
                  mostraToken();
          }
          if ( token == T_ERRO_LEX ) {
              JOptionPane.showMessageDialog( null, mensagemDeErro, "Erro Léxico", JOptionPane.ERROR_MESSAGE );
          } else {
              JOptionPane.showMessageDialog( null, "Análise Léxica concluída com sucesso", "Análise Léxica concluída", JOptionPane.INFORMATION_MESSAGE );
          }
          exibeTokens();
          gravaSaida( arqDestino );
          fechaFonte();
      } catch( FileNotFoundException fnfe ) {
          JOptionPane.showMessageDialog( null, "Arquivo não existe", "FileNotFoundException", JOptionPane.ERROR_MESSAGE );
      } catch( UnsupportedEncodingException uee ) {
          JOptionPane.showMessageDialog( null, "Erro desconhecido", "UnsupportedEncodingException", JOptionPane.ERROR_MESSAGE );
      } catch( IOException ioe ) {
          JOptionPane.showMessageDialog( null, "Erro de io: " + ioe.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE );
      } finally {
          System.out.println( "Execução concluída" );
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

  static void buscaProximoToken() throws IOException
  {
	//int i, j;
        
    StringBuffer sbLexema = new StringBuffer( "" );

    // Salto espa�oes enters e tabs at� o inicio do proximo token
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

		FiltroJoz3 filtro = new FiltroJoz3();
	    
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

		FiltroJoz3 filtro = new FiltroJoz3();
		    
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
	
	public static void acumulaToken( String tokenIdentificado ) {

		tokensIdentificados.append( tokenIdentificado );
		tokensIdentificados.append( "\n" );
		
	}
		
}

/**
 * Classe Interna para cria��o de filtro de sele��o
 */
class FiltroJoz3 extends FileFilter {

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
	 * Retorna quais extens�es poder�o ser escolhidas
	 */
	public String getDescription() {
		return "*.grm";
	}
	
	/**
	 * Retorna a parte com a extens�o de um arquivo
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
