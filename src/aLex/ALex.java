/*package aLex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ALex {

	private File fichero;
	private FileReader reader;
	private char car;
	private int n;
	private MT_AFD matriz;
	private Map<String, Integer> palabrasReservadas = new LinkedHashMap<>();
	private String error = "";
	private int nLinea = 1;

	public ALex(File fichero) {
		this.fichero = fichero;
		// Matriz de transiciones
		matriz = new MT_AFD();
		// Lista de palabras reservadas
		palabrasReservadas.put("int", 21);
		palabrasReservadas.put("boolean", 22);
		palabrasReservadas.put("if", 23);
		palabrasReservadas.put("for", 24);
		palabrasReservadas.put("let", 25);
		palabrasReservadas.put("string", 26);
		palabrasReservadas.put("print", 27);
		palabrasReservadas.put("input", 28);
		palabrasReservadas.put("return", 29);
		palabrasReservadas.put("function", 30);

		try {
			reader = new FileReader(fichero);
			n = reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public aLex.Token getToken() {
		int cod = 0;
		Object atr = null;
		try {
			// boolean fin = false;
			int valor = 0;
			int estado = 0;
			String palabra = "";
			if (n != -1) {
				car = (char) n;
		
				while (estado < 10 && n != -1) {
					// System.out.print(car);
					// System.out.println(estado);
					matriz.transicion(estado, car);
					estado = matriz.getEstado();
					// System.out.println(estado);

					if (matriz.getEstado() == -1) {
						System.err.println("Error lexico en linea " + nLinea + ": " + error);
						error = "";
						estado = 0;
					}
					switch (matriz.getAccion()) {
					case "X":
						valor = n - 48;
						n = reader.read();
						car = (char) n;
						break;
					case "Y":
						palabra = "" + car;
						n = reader.read();
						car = (char) n;
						break;
					case "LEER":
						n = reader.read();
						car = (char) n;
						break;
					case "Q":
						palabra = "";
						n = reader.read();
						car = (char) n;
					case "A":
						cod = 1;
						n = reader.read();
						car = (char) n;
						break;
					case "B":
						cod = 2;
						n = reader.read();
						car = (char) n;
						break;
					case "C":
						cod = 3;
						n = reader.read();
						car = (char) n;
						break;
					case "D":
						cod = 4;
						n = reader.read();
						car = (char) n;
						break;
					case "E":
						cod = 5;
						n = reader.read();
						car = (char) n;
						break;
					case "F":
						cod = 6;
						n = reader.read();
						car = (char) n;
						break;
					case "G":
						cod = 7;
						n = reader.read();
						car = (char) n;
						break;
					case "H":
						cod = 8;
						n = reader.read();
						car = (char) n;
						break;
					case "I":
						cod = 9;
						n = reader.read();
						car = (char) n;
						break;
					case "J":
						cod = 10;
						n = reader.read();
						car = (char) n;
						break;
					case "K":
						cod = 11;
						n = reader.read();
						car = (char) n;
						break;
					case "L":
						cod = 12;
						n = reader.read();
						car = (char) n;
						break;
					case "M":
						valor = valor * 10 + n - 48;
						n = reader.read();
						car = (char) n;
						break;
					case "N":
						if (valor > 32767)
							System.err.print("Error lexico linea " + nLinea + ": el numero supera el rango");
						else {
							cod = 14;
							atr = valor;
						}
						break;
					case "CONC":
						palabra += car;
						n = reader.read();
						car = (char) n;
						break;
					case "O":
						cod = 15;
						atr = palabra;
						n = reader.read();
						car = (char) n;
						break;
					case "P":
						int p;
						if ((p = palabrasReservadas.get(palabra)) != null) {
							cod = p.intValue();
							atr = null;
						} else {
							
							 p=buscaTS(palabra); 
							  if((p=buscaTS(palabra))!=null){ 
							  }else{
							   if(zona_decl){
							  p=anadirTS(palabra); 
							  }else{ p=anadirTSglobal(palabra); 
							  }
							   }
							 
							cod = 1;
							atr = p;
						}
						break;
					case "R":
						cod = 17;
						n = reader.read();
						car = (char) n;
						break;
					case "S":
						cod = 16;
						n = reader.read();
						car = (char) n;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new aLex.Token(cod, atr);
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
		}
	}

	public class MT_AFD {
		// Estado en el que estamos
		int estado;
		// Accion semantica que estamos ejecutando
		String accion;

		public MT_AFD() {
			estado = 0;
			accion = "";
		}

		public int getEstado() {
			return this.estado;
		}

		public String getAccion() {
			return this.accion;
		}

		public void transicion(int state, char car) {
			estado = state;
			switch (state) {
			// Primero definimos los estados
			case 0:
				if (esDig(car)) {
					estado = 1;
					accion = "X";
				} else if (esMin(car) || esMay(car)) {
					estado = 6;
					accion = "Y";
				} else if (esDel(car)) {
					if (car == '\n')
						nLinea++;
					estado = 0;
					accion = "LEER";

				} else {
					switch (car) {
					case '/':
						estado = 7;
						accion = "LEER";
						break;
					case '"':
						estado = 5;
						accion = "LEER";
						break;
					case '(':
						estado = 18;
						accion = "C";
						break;
					case ')':
						estado = 17;
						accion = "D";
						break;
					case '{':
						estado = 15;
						accion = "E";
						break;
					case '}':
						estado = 14;
						accion = "F";
						break;
					case '=':
						estado = 16;
						accion = "B";
						break;
					case '+':
						estado = 11;
						accion = "A";
						break;
					case '-':
						estado = 2;
						accion = "LEER";
						break;
					case '<':
						estado = 19;
						accion = "G";
						break;
					case '>':
						estado = 20;
						accion = "H";
						break;
					case '&':
						estado = 3;
						accion = "LEER";
						break;
					case '|':
						estado = 4;
						accion = "LEER";
						break;
					case ',':
						estado = 26;
						accion = "S";
						break;
					case ';':
						estado = 25;
						accion = "R";
						break;
					default:
						error = "caracter '" + car + "' no reconocido";
						estado = -1;
						accion = "LEER";
						break;
					}
				}
				break;
			case 1:
				if (esDig(car)) {
					estado = 1;
					accion = "M";
				} else {
					estado = 10;
					accion = "N";
				}
				break;
			case 2:
				if (car == '=') {
					estado = 12;
					accion = "L";
				} else {
					estado = 13;
					accion = "K";
				}
				break;
			case 3:
				if (car == '&') {
					estado = 21;
					accion = "I";
				} else {
					// Lanza mensaje de error
					error = "se esperaba el caracter '&'";
					estado = -1;
				}
				break;
			case 4:
				if (car == '|') {
					estado = 22;
					accion = "J";
				} else {
					// Lanza mensaje de error
					error = "se esperaba el caracter '|'";
					estado = -1;
				}
				break;
			case 5:
				if (car == '"') {
					estado = 23;
					accion = "O";
				} else
					accion = "LEER";
				break;
			case 6:
				if (esDig(car) || esMin(car) || esMay(car) || car == '_') {
					estado = 6;
					accion = "CONC";
				} else {
					estado = 24;
					accion = "P";
				}
				break;
			case 7:
				if (car == '*') {
					estado = 8;
					accion = "LEER";
				} else {
					// Lanza mensaje de error
					error = "se esperaba el caracter '*'";
					estado = -1;
				}
				break;
			case 8:
				if (car == '*') {
					estado = 9;
					accion = "LEER";
				} else {
					estado = 8;
					accion = "LEER";
				}
				break;
			case 9:
				if (car == '*') {
					estado = 9;
					accion = "LEER";
				} else if (car == '/') {
					estado = 0;
					accion = "LEER";
				} else {
					estado = 8;
					accion = "LEER";
				}
				break;

			}
		}

		private boolean esDig(char car) {
			return (((int) car) >= 48 && ((int) car) <= 57);
		}

		private boolean esMin(char car) {
			return (((int) car) >= 97 && ((int) car) <= 122) || car == 'é' || (((int) car) >= 160 && ((int) car) <= 164)
					|| car == 'ü';
		}

		private boolean esMay(char car) {
			return (((int) car) >= 65 && ((int) car) <= 90) || car == 'Á' || car == 'É' || car == 'Í' || car == 'Ó'
					|| car == 'Ú' || car == 'Ü';
		}

		private boolean esDel(char car) {
			return car == '\t' || car == ' ' || car == '\n';
		}

	}

	public class Token {

		private int cod;
		private Object atributo;

		public Token(int codigo, Object atr) {
			cod = codigo;
			atributo = atr;
		}

		public String toString() {
			String res;
			if (atributo == null) {
				res = "< " + cod + ", > //token de ";
			} else
				res = "< " + cod + ", " + atributo.toString() + " > //token de ";
			String coment = "";
			switch (cod) {
			case 0:
				coment = "fin de fichero";
				break;
			case 1:
				coment = "identificador";
				break;
			case 2:
				coment = "asignacion";
				break;
			case 3:
				coment = "abrir parentesis";
				break;
			case 4:
				coment = "cerrar parentesis";
				break;
			case 5:
				coment = "abrir corchetes";
				break;
			case 6:
				coment = "cerrar corchetes";
				break;
			case 7:
				coment = "suma";
				break;
			case 8:
				coment = "resta";
				break;
			case 9:
				coment = "asignacion resta";
				break;
			case 10:
				coment = "menor que";
				break;
			case 11:
				coment = "mayor que";
				break;
			case 12:
				coment = "y logico";
				break;
			case 13:
				coment = "o logico";
				break;
			case 14:
				coment = "entero";
				break;
			case 15:
				coment = "cadena";
				break;
			case 16:
				coment = "coma";
				break;
			case 17:
				coment = "punto y coma";
				break;
			case 18:
				coment = "";
				break;
			case 19:
				coment = "";
				break;
			case 20:
				coment = "";
				break;
			case 21:
				coment = "declarando int";
				break;
			case 22:
				coment = "declarando boolean";
				break;
			case 23:
				coment = "if";
				break;
			case 24:
				coment = "for";
				break;
			case 25:
				coment = "let";
				break;
			case 26:
				coment = "string";
				break;
			case 27:
				coment = "print";
				break;
			case 28:
				coment = "input";
				break;
			case 29:
				coment = "return";
				break;
			case 30:
				coment = "function";

			}
			return res + coment;
		}

		public int getCodigo() {
			return this.cod;
		}

		public Object getAtributo() {
			return this.atributo;
		}
	}

}*/
