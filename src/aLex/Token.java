package aLex;

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
			coment = "";
			break;
		case 22:
			coment = "";
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
			break;
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
