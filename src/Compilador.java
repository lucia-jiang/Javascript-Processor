import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jaime
 * @author Xin Ru
 * @author Lucia
 */

public class Compilador {

	private static TS tsG;
	private static boolean zona_decl;
	private static TS tsAct;
	private int despG;
	private int despL;

	private int nLinea = 0;
	private int nTabla = 1;

	private static PrintWriter parse;
	private static PrintWriter ts;

	public static void main(String[] args) {
		ALex aLex = null;
		ASint anSint = null;
		PrintWriter tokens = null;
		try {
			Compilador comp = new Compilador();
			String nombre;
			if (args.length == 0) {
				nombre = "/Users/lucia/Desktop/PdL/Proyecto PdL/ej2.txt";
			}
			else {
				nombre = args[0];
			}
			
			File fichero = new File(nombre);

			tokens = new PrintWriter("/Users/lucia/Desktop/PdL/Proyecto PdL/tokens.txt", "UTF-8");
			ts = new PrintWriter("/Users/lucia/Desktop/PdL/Proyecto PdL/ts.txt", "UTF-8");
			parse = new PrintWriter("/Users/lucia/Desktop/PdL/Proyecto PdL/parse.txt", "UTF-8");

			parse.write("D ");
			aLex = comp.new ALex(fichero, tokens);
			anSint = comp.new ASint(aLex);
			anSint.P1();

		} catch (FileNotFoundException e) {
			System.err.println("No existe el fichero o es una carpeta.");

		} catch (Exception e) {
		}
		try {
			tokens.close();
			ts.close();
			parse.close();
			aLex.close();

		} catch (Exception e) {
		}

	}

	public class ASint {
		private ALex aLex;
		private Token sig_token;
		private Map<String, Integer> codToken = new LinkedHashMap<>();

		public ASint(ALex aLex) {
			this.aLex = aLex;
			this.sig_token = aLex.getToken();

			// Codigos de tokens
			codToken.put("$", 0);
			codToken.put("id", 1);
			codToken.put("=", 2);
			codToken.put("(", 3);
			codToken.put(")", 4);
			codToken.put("{", 5);
			codToken.put("}", 6);
			codToken.put("+", 7);
			codToken.put("-", 8);
			codToken.put("-=", 9);
			codToken.put("<", 10);
			codToken.put(">", 11);
			codToken.put("&&", 12);
			codToken.put("||", 13);
			codToken.put("entero", 14);
			codToken.put("cadena", 15);
			codToken.put(",", 16);
			codToken.put(";", 17);
			codToken.put("int", 21);
			codToken.put("boolean", 22);
			codToken.put("if", 23);
			codToken.put("for", 24);
			codToken.put("let", 25);
			codToken.put("string", 26);
			codToken.put("print", 27);
			codToken.put("input", 28);
			codToken.put("return", 29);
			codToken.put("function", 30);
		}

		public void P1() throws Exception {
			parse.write("1 ");
			tsG = new TS();
			tsAct = tsG;
			despG = 0;
			zona_decl = false;
			P();
			// destruir tsG
			ts.write("TABLA PRINCIPAL # " + 1 + ":\n");
			ts.write(tsG.toString());
			tsG = null;
		}

		private void P() throws Exception {
			if (sigIgual("let") || sigIgual("if") || sigIgual("for") || sigIgual("id") || sigIgual("print")
					|| sigIgual("return") || sigIgual("input")) {
				parse.write("2 ");
				B();
				P();
			} else if (sigIgual("function")) {
				parse.write("3 ");
				F();
				P();
			} else if (sigIgual("$")) {
				parse.write("4 ");
				equiparar("$");
			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": solo se permiten declaracines, condicionales, bucles, asignaciones, instrucciones de entrada o salida, funciones, salida de funciones o final de fichero.");
				throw new Exception();
			}
		}

		private Tipo[] B() throws Exception {
			Tipo[] b = new Tipo[2];
			Integer idPos = -1;
			Object t[];
			if (sigIgual("let")) {
				zona_decl = true;
				equiparar("let");
				parse.write("5 ");
				t = T();
				idPos = ((Integer) sig_token.getAtributo()).intValue();
				equiparar("id");
				equiparar(";");
				if (buscaTS(idPos)) {
					b[0] = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea + ": Ya existe la variable con ese id.");
				} else {

					if (tsAct == tsG) {
						insertarTipoTSG(idPos, (Tipo) t[0]);
						insertarDespTSG(idPos, despG);
						despG += (Integer) t[1];
					} else {
						insertarTipoTS(idPos, (Tipo) t[0]);
						insertarDespTS(idPos, despL);
						despL += (Integer) t[1];
					}
					b[0] = Tipo.ok;
					b[1] = Tipo.vacio;

				}
				zona_decl = false;

			} else if (sigIgual("if")) {
				parse.write("6 ");
				equiparar("if");
				equiparar("(");
				Tipo e = E();
				equiparar(")");
				Tipo[] s = S();

				if (e == Tipo.log) {
					b[0] = s[0];
				} else {
					b[0] = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": La condición debe ser una expresión lógica.");

				}
				b[1] = s[1];
			}

			else if (sigIgual("id") || sigIgual("print") || sigIgual("input") || sigIgual("return")) {
				parse.write("7 ");
				Tipo[] s = S();
				b[0] = s[0];
				b[1] = s[1];

			} else if (sigIgual("for")) {
				equiparar("for");
				equiparar("(");
				parse.write("8 ");

				Tipo m = M();
				equiparar(";");
				Tipo e = E();

				equiparar(";");
				Tipo n = N();
				equiparar(")");
				equiparar("{");
				Object[] c = C();
				equiparar("}");

				b[1] = (Tipo) c[1];
				if (e != Tipo.log) {
					b[0] = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": La condición debe ser una expresión lógica.");
				} else if (m == Tipo.ok && n != Tipo.error) {
					b[0] = (Tipo) c[0];

				} else {
					b[0] = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": Implementación incorrecta del bucle for.");
				}

			} else {
				b[0] = Tipo.error;
				b[1] = Tipo.error;
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": solo se permiten declaración de variables, condicionales, sentencias que incluyan identificadores de las varibales, instrucciones de entreda o salida, retorno de valores o bucles for. ");
				throw new Exception();

			}
			return b;
		}

		private Object[] T() throws Exception {
			Object[] t = new Object[2];
			if (sigIgual("string")) {
				equiparar("string");
				parse.write("9 ");
				t[0] = Tipo.cadena;
				t[1] = 64;

			} else if (sigIgual("int")) {
				equiparar("int");
				parse.write("10 ");
				t[0] = Tipo.ent;
				t[1] = 1;

			} else if (sigIgual("boolean")) {
				equiparar("boolean");
				parse.write("11 ");
				t[0] = Tipo.log;
				t[1] = 1;

			} else {
				t[0] = Tipo.error;
				t[1] = Tipo.error;
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": solo se permiten las variables de tipo básico (string, int o boolean).");
				throw new Exception();

			}
			return t;
		}

		private Tipo[] S() throws Exception {
			Tipo[] s = new Tipo[2];
			if (sigIgual("id")) {

				int idPos = ((Integer) sig_token.getAtributo()).intValue();

				equiparar("id");
				Object[][] s1;
				parse.write("12 ");
				s1 = S1();

				if (!buscaTS(idPos)) {
					if (s1[0][0] == Tipo.ent) {
						insertarTipoTSG(idPos, Tipo.ent);
						insertarDespTSG(idPos, despG++);
						s[0] = Tipo.ok;
					} else {
						s[0] = Tipo.error;
						System.err.println("Error semántico en línea " + nLinea + ": El valor debe ser un entero.");
					}

				} else if (((Tipo) s1[0][0]) == Tipo.ok && arraysIguales(buscarArgTS(idPos), (Tipo[]) s1[2])
						|| s1[0][0] == buscarTipoTS(idPos)) {
					s[0] = Tipo.ok;

				} else {
					s[0] = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea
							+ ": La llamada de la función es incorrecta o la asignación es incorrecta.");
				}
				s[1] = (Tipo) s1[1][0];
			} else if (sigIgual("print")) {
				parse.write("13 ");
				Tipo e;
				equiparar("print");
				equiparar("(");
				e = E();
				equiparar(")");
				equiparar(";");
				if (e == Tipo.cadena || e == Tipo.ent) {
					s[0] = Tipo.ok;
				} else {
					s[0] = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": Solo se pueden imprimir cadenas o enteros.");
				}
				s[1] = Tipo.vacio;

			} else if (sigIgual("input")) {
				parse.write("14 ");
				equiparar("input");
				equiparar("(");

				int idPos = ((Integer) sig_token.getAtributo()).intValue();

				equiparar("id");
				equiparar(")");

				if (!buscaTS(idPos)) {
					s[0] = Tipo.ok;

					insertarTipoTSG(idPos, Tipo.ent);
					insertarDespTSG(idPos, despG);
					despG += 1;
				} else if (buscarTipoTS(idPos) == Tipo.ent || buscarTipoTS(idPos) == Tipo.cadena) {
					s[0] = Tipo.ok;
				} else {
					s[0] = Tipo.error;
					System.err
							.println("Error semántico en línea " + nLinea + ": Solo se pueden leer enteros o cadenas.");
				}
				equiparar(";");
				s[1] = Tipo.vacio;

			} else if (sigIgual("return")) {
				parse.write("15 ");
				equiparar("return");
				Tipo x = X();
				equiparar(";");
				s[1] = x;
				if (x == Tipo.error) {
					s[0] = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea + ": La expresión es errónea.");
				} else {
					s[0] = Tipo.ok;
				}

			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": solo se permiten sentencias que incluyan identificadores de las variables, instrucciones de entrada o salida y retorno de valores.");
				throw new Exception();
			}
			return s;
		}

		private Tipo[][] S1() throws Exception {
			Tipo[][] s1 = new Tipo[3][];
			s1[0] = new Tipo[1];
			s1[1] = new Tipo[1];

			if (sigIgual("=")) {
				parse.write("16 ");
				equiparar("=");
				Tipo e = E();
				equiparar(";");
				s1[0][0] = e;
				s1[1][0] = Tipo.vacio;
				s1[2] = new Tipo[1];
				s1[2][0] = Tipo.vacio;

			} else if (sigIgual("(")) {
				parse.write("17 ");
				equiparar("(");
				Tipo[] l = L();
				equiparar(")");
				equiparar(";");
				s1[0][0] = Tipo.ok;
				s1[1][0] = Tipo.vacio;
				s1[2] = new Tipo[1];
				s1[2] = l;

			} else if (sigIgual("-=")) {
				parse.write("18 ");
				equiparar("-=");
				Tipo e = E();
				equiparar(";");
				if (e == Tipo.ent) {
					s1[0][0] = e;
				} else {
					s1[0][0] = Tipo.error;
					System.err
							.println("Error semántico en línea " + nLinea + ": Solo se pueden restar números enteros.");
				}
				s1[1][0] = Tipo.vacio;
				s1[2] = new Tipo[1];
				s1[2] = new Tipo[1];
				s1[2][0] = Tipo.vacio;

			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": solo se permiten asignación de variables o llamadas a funciones.");
				throw new Exception();
			}
			return s1;
		}

		private Tipo X() throws Exception {
			Tipo x;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("boolean") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("19 ");
				x = E();
			} else {
				parse.write("20 ");
				x = Tipo.vacio;
			}
			return x;
		}

		private Tipo[] C() throws Exception {
			Tipo[] c = new Tipo[2];
			if (sigIgual("let") || sigIgual("if") || sigIgual("for") || sigIgual("id") || sigIgual("print")
					|| sigIgual("input") || sigIgual("return")) {
				parse.write("21 ");
				Tipo[] b, cc;
				b = B();
				cc = C();
				if (b[0] == Tipo.ok) {
					c[0] = cc[0];
				} else {
					c[0] = Tipo.error;
				}
				if (cc[1] == Tipo.vacio || cc[1] == b[1]) {
					c[1] = b[1];
				} else if (b[1] == Tipo.vacio) {
					c[1] = cc[1];
				} else {
					c[1] = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": Error en los valores devueltos por la función.");

				}
			} else {
				parse.write("22 ");
				c[0] = Tipo.ok;
				c[1] = Tipo.vacio;
			}
			return c;
		}

		private Tipo[] L() throws Exception {
			Tipo[] l;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("boolean") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("23 ");
				Tipo e = E();
				Tipo[] q = Q();
				if (q[0] == Tipo.vacio) {
					l = new Tipo[1];
					l[0] = e;
				} else {
					l = new Tipo[q.length + 1];
					for (int i = 1; i <= q.length; i++) {
						l[i] = q[i - 1];
					}
					l[0] = e;
				}
			} else {
				parse.write("24 ");
				l = new Tipo[1];
				l[0] = Tipo.vacio;
			}
			return l;
		}

		private Tipo[] Q() throws Exception {
			Tipo[] q;
			if (sigIgual(",")) {
				equiparar(",");
				parse.write("25 ");
				Tipo e = E();
				Tipo[] qq = Q();
				if (qq[0] == Tipo.vacio) {
					q = new Tipo[1];
					q[0] = e;
				} else {
					q = new Tipo[qq.length + 1];
					for (int i = 1; i < q.length; i++) {
						q[i] = qq[i - 1];
					}

					q[0] = e;
				}

			} else {
				parse.write("26 ");
				q = new Tipo[1];
				q[0] = Tipo.vacio;
			}
			return q;
		}

		private Tipo F() throws Exception {
			Tipo f;
			int idPos;
			Tipo h;
			Tipo[] a;
			Tipo[] c;
			if (sigIgual("function")) {
				parse.write("27 ");
				if (tsAct != tsG) {
					f = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": No se permite crear una función dentro de otra.");
					return f;
				}
				equiparar("function");
				idPos = ((Integer) sig_token.getAtributo()).intValue();

				equiparar("id");

				if (buscaTS(idPos)) {
					f = Tipo.error;
				} else {
					String etiq = insertarTS(idPos);
					tsAct = new TS();
					tsAct.setEtiqueta(etiq);
					despL = 0;
					f = Tipo.vacio;
				}
				h = H();
				zona_decl = true;
				equiparar("(");
				a = A();
				zona_decl = false;
				equiparar(")");

				if (f != Tipo.error) {
					insertarTipoTS(idPos, a, h);
				}
				equiparar("{");
				c = C();
				equiparar("}");
				if (f != Tipo.error) {
					if (c[0] == Tipo.error) {
						System.err.println("Sentencias incorrectas");
					}
					if (c[1] != h) {
						System.err.println(
								"Error semántico en línea " + nLinea + ": El tipo del valor de retorno es incorrecto.");
					}
					destruirTS(tsAct);
					tsAct = tsG;
				}

			} else {
				System.err.println(
						"Error sintáctico en línea " + nLinea + ": se esperaba la declaración de una funcion.");
				f = Tipo.error;
				throw new Exception();

			}
			return f;
		}

		private Tipo H() throws Exception {
			Tipo h;

			if (sigIgual("int") || sigIgual("string") || sigIgual("boolean")) {
				parse.write("28 ");
				Object t[] = T();
				h = (Tipo) t[0];
			} else {
				parse.write("29 ");
				h = Tipo.vacio;
			}
			return h;
		}

		private Tipo[] A() throws Exception {

			Tipo a[];
			if (sigIgual("int") || sigIgual("string") || sigIgual("boolean")) {
				parse.write("30 ");
				Object[] t = T();
				int idPos = ((Integer) sig_token.getAtributo()).intValue();
				equiparar("id");
				Tipo[] k = K();
				if (!buscaTS(idPos)) {
					insertarTipoTS(idPos, (Tipo) t[0]);
					insertarDespTS(idPos, despL);
					despL += (Integer) t[1];
					a = new Tipo[k.length];
					for (int i = 1; i < k.length; i++) {
						a[i] = k[i - 1];
					}
					a[0] = (Tipo) t[0];
				} else {
					a = new Tipo[1];
					a[0] = Tipo.error;

				}

			} else {
				parse.write("31 ");
				a = new Tipo[1];
				a[0] = Tipo.vacio;
			}

			return a;
		}

		private Tipo[] K() throws Exception {
			Tipo[] k;
			if (sigIgual(",")) {
				parse.write("32 ");
				equiparar(",");
				Object[] t = T();

				int idPos = ((Integer) sig_token.atributo).intValue();
				equiparar("id");
				Tipo[] kk = K();
				if (!buscaTS(idPos)) {
					insertarTipoTS(idPos, (Tipo) t[0]);
					insertarDespTS(idPos, despL);
					despL += (Integer) t[1];
					k = new Tipo[kk.length + 1];
					k[0] = (Tipo) t[0];
					for (int i = 1; i < k.length; i++) {
						k[i] = kk[i - 1];
					}
				} else {
					k = new Tipo[1];
					k[0] = Tipo.error;
				}

			} else {
				parse.write("33 ");
				k = new Tipo[1];
				k[0] = Tipo.vacio;
			}
			return k;
		}

		private Tipo M() throws Exception {
			Tipo m;
			if (sigIgual("id")) {
				int idPos = ((Integer) sig_token.atributo).intValue();
				parse.write("34 ");

				equiparar("id");
				equiparar("=");
				Tipo e = E();
				if (!buscaTS(idPos)) {
					if (e == Tipo.ent) {
						insertarTipoTSG(idPos, Tipo.ent);
						insertarDespTSG(idPos, despG);
						despG++;
						m = Tipo.ok;
					} else {
						m = Tipo.error;
						System.err.println(
								"Error semántico en línea " + nLinea + ": el valor asignado debe ser un entero.");
					}
				} else if (e == buscarTipoTS(idPos))
					m = Tipo.ok;
				else {
					m = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea
							+ ": el tipo del valor asignado no coincide con el tipo de la variable.");
				}

			} else {
				parse.write("35 ");
				m = Tipo.vacio;
			}
			return m;
		}

		private Tipo E() throws Exception {
			Tipo e = Tipo.error;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("36 ");
				Tipo o = O();
				Tipo e1 = E1();
				if (e1 == Tipo.vacio)
					e = o;
				else if (e1 == Tipo.log && o == Tipo.log)
					e = Tipo.log;
				else {
					System.err.println(
							"Error semántico en línea " + nLinea + ": los tipos de la expresión no concuerdan.");
				}
			} else {
				System.err.println("Error sintáctico en línea " + nLinea + ": la expresión es incorrecta.");
				throw new Exception();

			}
			return e;
		}

		private Tipo E1() throws Exception {
			Tipo e1 = Tipo.vacio;
			if (sigIgual("||")) {
				equiparar("||");
				parse.write("37 ");
				Tipo o = O();
				Tipo e2 = E1();
				if ((e2 == Tipo.vacio || e2 == Tipo.log) && o == Tipo.log)
					e1 = Tipo.log;
				else {
					e1 = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea
							+ ": solo se puede utilizar el operador || con expresiones lógicas.");
				}
			} else {
				parse.write("38 ");
			}
			return e1;
		}

		private Tipo O() throws Exception {
			Tipo o;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("39 ");
				Tipo r = R();
				Tipo o1 = O1();
				if (o1 == Tipo.vacio)
					o = r;
				else if (r == Tipo.log && o1 == Tipo.log)
					o = Tipo.log;
				else {
					o = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": los tipos de la expresión no concuerdan.");
				}
			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": la expresión es incorrecta. Se esperaba un identificador, un entero, una cadena o '('.");
				o = Tipo.error;
				throw new Exception();

			}
			return o;
		}

		private Tipo O1() throws Exception {
			Tipo o1;
			if (sigIgual("&&")) {
				parse.write("40 ");
				equiparar("&&");
				Tipo r = R();
				Tipo o2 = O1();
				if ((o2 == Tipo.vacio || o2 == Tipo.log) && r == Tipo.log)
					o1 = Tipo.log;
				else {
					o1 = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea
							+ ": solo se puede utlizar el operador && con expresiones lógicas.");
				}
			} else {
				parse.write("41 ");
				o1 = Tipo.vacio;
			}
			return o1;
		}

		private Tipo R() throws Exception {
			Tipo r;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("42 ");
				Tipo u = U();
				Tipo r1 = R1();
				if (r1 == Tipo.vacio)
					r = u;
				else if (u == Tipo.ent && r1 == Tipo.log)
					r = Tipo.log;
				else {
					r = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": los tipos de la expresión no concuerdan.");
				}
			} else {
				r = Tipo.error;
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": la expresión es incorrecta. Se esperaba un identificador, un entero, una cadena o '('.");
				throw new Exception();

			}
			return r;
		}

		private Tipo R1() throws Exception {
			Tipo r1;
			if (sigIgual("<")) {
				equiparar("<");
				parse.write("43 ");
				Tipo u = U();
				Tipo r2 = R1();
				if (r2 == Tipo.vacio && u == Tipo.ent)
					r1 = Tipo.log;
				else {
					r1 = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": solo se pueden comparar números enteros.");
				}

			} else if (sigIgual(">")) {
				equiparar(">");
				parse.write("44 ");
				Tipo u = U();
				Tipo r2 = R1();
				if (r2 == Tipo.vacio && u == Tipo.ent)
					r1 = Tipo.log;
				else {
					r1 = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": solo se pueden comparar números enteros.");
				}

			} else {
				parse.write("45 ");
				r1 = Tipo.vacio;
			}
			return r1;
		}

		private Tipo U() throws Exception {
			Tipo u;
			if (sigIgual("id") || sigIgual("entero") || sigIgual("cadena") || sigIgual("(")) {
				parse.write("46 ");
				Tipo v = V();
				Tipo u1 = U1();
				if (u1 == Tipo.vacio)
					u = v;
				else if (u1 == Tipo.ent && v == Tipo.ent)
					u = Tipo.ent;
				else {
					u = Tipo.error;
					System.err.println(
							"Error semántico en línea " + nLinea + ": los tipos de la expresión no concuerdan.");
				}
			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": la expresión es incorrecta. Se esperaba el identificador de una variable, un entero, una cadena o un valor lógico.");
				throw new Exception();
			}
			return u;
		}

		private Tipo U1() throws Exception {
			Tipo u1 = Tipo.vacio;
			if (sigIgual("+")) {
				equiparar("+");
				parse.write("47 ");
				Tipo v = V();
				Tipo u2 = U1();
				if ((u2 == Tipo.vacio || u2 == Tipo.ent) && v == Tipo.ent)
					u1 = Tipo.ent;
				else {
					u1 = Tipo.error;
					System.err
							.println("Error semántico en línea " + nLinea + ": solo se pueden sumar números enteros.");
				}

			} else if (sigIgual("-")) {
				equiparar("-");
				parse.write("48 ");
				Tipo v = V();
				Tipo u2 = U1();
				if ((u2 == Tipo.vacio || u2 == Tipo.ent) && v == Tipo.ent)
					u1 = Tipo.ent;
				else {
					u1 = Tipo.error;
					System.err
							.println("Error semántico en línea " + nLinea + ": solo se pueden restar números enteros.");
				}

			} else {
				parse.write("49 ");
			}
			return u1;
		}

		private Tipo V() throws Exception {
			Tipo v;
			if (sigIgual("id")) {
				int idPos = ((Integer) sig_token.getAtributo()).intValue();
				equiparar("id");
				parse.write("50 ");
				Tipo[] v1 = V1();
				if (v1[0] == Tipo.vacio) {
					if (!buscaTS(idPos)) {
						insertarTipoTS(idPos, Tipo.ent);
						insertarDespTS(idPos, despG);
						despG++;
						v = Tipo.ent;
					} else {
						v = buscarTipoTS(idPos);
					}
				} else if (arraysIguales(buscarArgTS(idPos), v1)) {
					v = tipoRetTS(idPos);
				} else {
					v = Tipo.error;
					System.err.println(
							"Error semántico en la línea " + nLinea + ": la llamada de la función es incorrecta.");
				}

			} else if (sigIgual("entero")) {
				equiparar("entero");
				parse.write("51 ");
				v = Tipo.ent;

			} else if (sigIgual("cadena")) {
				equiparar("cadena");
				parse.write("52 ");
				v = Tipo.cadena;

			} else if (sigIgual("(")) {
				equiparar("(");
				parse.write("53 ");
				Tipo e = E();
				equiparar(")");
				v = e;

			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": la expresión es incorrecta. Se esperaba el identificador de una variable, un entero, un valor lógico o un paréntesis.");
				throw new Exception();
			}

			return v;
		}

		private Tipo[] V1() throws Exception {
			Tipo[] v1;
			if (sigIgual("(")) {
				equiparar("(");
				parse.write("55 ");
				Tipo[] l = L();
				equiparar(")");
				v1 = l;

			} else {
				parse.write("54 ");
				v1 = new Tipo[1];
				v1[0] = Tipo.vacio;
			}
			return v1;

		}

		private Tipo N() throws Exception {
			Tipo n, n1;
			if (sigIgual("id")) {
				parse.write("56 ");
				int idPos = ((Integer) this.sig_token.atributo).intValue();
				equiparar("id");
				n1 = N1();
				if (!buscaTS(idPos)) {
					n = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea
							+ ": el contador del bucle debe estar definido previamente.");
				} else {
					if (buscarTipoTS(idPos) == n1)
						n = Tipo.ok;
					else {
						n = Tipo.error;
						System.err.println("Error semántico en línea " + nLinea
								+ ": la actualización del contador es incorrecta.");
					}
				}

			} else {
				n = Tipo.vacio;
				parse.write("57 ");
			}
			return n;
		}

		private Tipo N1() throws Exception {
			Tipo n1;
			if (sigIgual("=")) {
				parse.write("58 ");
				equiparar("=");
				n1 = E();

			} else if (sigIgual("-=")) {
				parse.write("59 ");
				equiparar("-=");
				Tipo e = E();
				if (e == Tipo.ent)
					n1 = Tipo.ent;
				else {
					n1 = Tipo.error;
					System.err.println("Error semántico en línea " + nLinea + ": solo se pueden restar enteros.");
				}

			} else {
				System.err.println("Error sintáctico en línea " + nLinea
						+ ": se esperaba una asignación o la asignación con resta.");
				throw new Exception();
			}
			return n1;
		}

		private boolean equiparar(String s) throws Exception {
			boolean res;
			if (res = sigIgual(s))
				this.sig_token = this.aLex.getToken();
			else {
				System.err.println("Error sintáctico en línea " + nLinea + ": se esperaba '" + s + "' ");
				throw new Exception();
			}
			return res;
		}

		private boolean sigIgual(String s) {
			return this.sig_token.getCodigo() == this.codToken.get(s);
		}
	}

	public class ALex {

		private FileReader reader;
		private char car;
		private int n;
		private MT_AFD matriz;
		private Map<String, Integer> palabrasReservadas = new LinkedHashMap<>();
		private String error = "";
		private PrintWriter tokens;

		public ALex(File fichero, PrintWriter tokens) throws FileNotFoundException, IOException {
			// Matriz de transiciones
			matriz = new MT_AFD();
			this.tokens = tokens;
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

			reader = new FileReader(fichero);
			leer();

		}

		public Token getToken() {
			int cod = 0;
			Object atr = null;
			try {
				int valor = 0;
				int estado = 0;
				String palabra = "";
				if (n != -1) {
					car = (char) n;

					while (estado < 10 && n != -1) {

						matriz.transicion(estado, car);
						estado = matriz.getEstado();

						if (matriz.getEstado() == -1) {
							System.err.println("Error léxico en línea " + nLinea + ": " + error);
							error = "";
							estado = 0;
						}
						switch (matriz.getAccion()) {
						case "X":
							valor = n - 48;
							leer();

							break;
						case "Y":
							palabra = "" + car;
							leer();

							break;
						case "LEER":
							leer();

							break;
						case "A":
							cod = 7;
							leer();

							break;
						case "B":
							cod = 2;
							leer();

							break;
						case "C":
							cod = 3;
							leer();

							break;
						case "D":
							cod = 4;
							leer();

							break;
						case "E":
							cod = 5;
							leer();

							break;
						case "F":
							cod = 6;
							leer();

							break;
						case "G":
							cod = 10;
							leer();

						case "H":
							cod = 11;
							leer();

							break;
						case "I":
							cod = 12;
							leer();

							break;
						case "J":
							cod = 13;
							leer();

							break;
						case "K":
							cod = 8;
							break;
						case "L":
							cod = 9;
							leer();

							break;
						case "M":
							valor = valor * 10 + n - 48;
							leer();

							break;
						case "N":
							if (valor > 32767)
								System.err.println("Error léxico línea " + nLinea + ": el numero supera el rango");
							else {
								cod = 14;
								atr = valor;
							}
							break;
						case "CONC":
							palabra += car;
							leer();

							break;
						case "O":
							cod = 15;
							atr = palabra + '"';
							leer();

							break;
						case "P":
							Integer p;
							if ((p = palabrasReservadas.get(palabra)) != null) {
								cod = p.intValue();
								atr = null;
							} else {

								if ((p = tsAct.contiene(palabra)) == null) {
									if (zona_decl) {
										p = tsAct.insertar(new Campos(palabra, null, -1));
									} else if ((p = tsG.contiene(palabra)) == null) {
										p = tsG.insertar(new Campos(palabra, null, -1));
									}

								}

								cod = 1;
								atr = p;
							}
							break;
						case "R":
							cod = 17;
							leer();
							break;
						case "S":
							cod = 16;
							leer();
							break;
						}
					}
				}
			} catch (Exception e) {
			}
			Token token = new Token(cod, atr);
			tokens.write(token.toString() + '\n');
			return token;
		}

		public void close() {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		
		private void leer() throws IOException {
			n = reader.read();
			if (n!=-1) {
			car = (char) n;
			if (car == '\n')
				nLinea++;
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
							accion = "CONC";
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
						error = "se esperaba el caracter '&'. El 'y' lógico se escribe '&&'.";
						estado = -1;
					}
					break;
				case 4:
					if (car == '|') {
						estado = 22;
						accion = "J";
					} else {
						// Lanza mensaje de error
						error = "se esperaba el caracter '|'. El 'or' lógico se escribe '||'.";
						estado = -1;
					}
					break;
				case 5:
					if (car == '"') {
						estado = 23;
						accion = "O";
					} else
						accion = "CONC";
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
						error = "se esperaba el caracter '*'. Solo están permitidos los comentarios del tipo /* <comentario> */.";
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
				return (((int) car) >= 97 && ((int) car) <= 122) || car == 'é'
						|| (((int) car) >= 160 && ((int) car) <= 164) || car == 'ü';
			}

			private boolean esMay(char car) {
				return (((int) car) >= 65 && ((int) car) <= 90) || car == 'Á' || car == 'É' || car == 'Í' || car == 'Ó'
						|| car == 'Ú' || car == 'Ü';
			}

			private boolean esDel(char car) {
				return car == '\t' || car == ' ' || car == '\n' || car == '\r';
			}

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
				coment = "asignación";
				break;
			case 3:
				coment = "abrir paréntesis";
				break;
			case 4:
				coment = "cerrar paréntesis";
				break;
			case 5:
				coment = "abrir llaves";
				break;
			case 6:
				coment = "cerrar llaves";
				break;
			case 7:
				coment = "suma";
				break;
			case 8:
				coment = "resta";
				break;
			case 9:
				coment = "asignación resta";
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

	private boolean buscaTS(int pos) {
		if (zona_decl) { // Si estamos en zona de declaración solo vamos a insertar en la TSL
			return (tsAct.contiene(pos) && tsAct.get(pos).getTipo() != null);
		} else {
			return (tsAct.contiene(pos) && tsAct.get(pos).getTipo() != null)
					|| (tsG.contiene(pos) && tsG.get(pos).getTipo() != null);
		}
	}

	private void insertarTipoTS(int pos, Tipo tipo) {
		tsAct.insertarTipo(pos, tipo);
	}

	private void insertarDespTS(int pos, Integer desp) {
		tsAct.insertarDesp(pos, desp);
	}

	private void insertarTipoTSG(int pos, Tipo tipo) {
		tsG.insertarTipo(pos, tipo);
	}

	private void insertarDespTSG(int pos, Integer desp) {
		tsG.insertarDesp(pos, desp);
	}

	private Tipo buscarTipoTS(int pos) {
		return tsAct.getTabla().containsKey(pos) ? tsAct.buscarTipo(pos) : tsG.buscarTipo(pos);
	}

	private Tipo[] buscarArgTS(int pos) {
		return tsG.buscarArg(pos);
	}

	private String insertarTS(int pos) {
		return tsG.insertarFuncion(pos, ++nTabla);
	}

	private void insertarTipoTS(int idPos, Tipo[] a, Tipo h) {
		tsG.insertarTipoFun(idPos, a, h);
	}

	private void destruirTS(TS tabla) {
		ts.write(tabla.getEtiqueta() + ":\n");
		ts.write(tabla.toString() + "\n\n\n");
	}

	private Tipo tipoRetTS(int pos) {
		return tsG.get(pos).getTipoDev();
	}

	private boolean arraysIguales(Tipo a[], Tipo b[]) {
		boolean iguales = a != null && b != null && a.length == b.length;
		for (int i = 0; i < a.length && iguales; i++) {
			iguales = a[i] == b[i];
		}
		return iguales;
	}
}