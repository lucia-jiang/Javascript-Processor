import java.util.HashMap;
import java.util.Map;

/**
 * @author Jaime
 * @author Xin Ru
 * @author Lucia
 */

enum Tipo {
	ent, log, cadena, ok, error, vacio, funcion,
}

public class TS {

	private Map<Integer, Campos> tabla;
	// private int despl = 0;
	String etiqueta;

	public TS() {
		tabla = new HashMap<>();
	}

	public int insertar(Campos campos) {
		int pos = campos.getNombre().hashCode();
		tabla.put(pos, campos);
		return pos;
	}

	public void insertarTipo(int pos, Tipo tipo) {
		tabla.get(pos).setTipo(tipo);
	}

	public void insertarDesp(int pos, int desp) {
		tabla.get(pos).setDespl(desp);
	}

	public Tipo buscarTipo(int pos) {
		return tabla.get(pos).getTipo();
	}

	public Tipo[] buscarArg(int pos) {
		return tabla.get(pos).getTipoDeParam();
	}

	public Integer contiene(String nombre) {
		if (tabla.containsKey(nombre.hashCode()))
			return nombre.hashCode();
		else
			return null;
	}

	public boolean contiene(int pos) {
		return tabla.containsKey(pos);
	}

	public String insertarFuncion(int pos, int nTabla) {
		String s = "TABLA FUNCION " + tabla.get(pos).getNombre() + " # " + nTabla;
		tabla.get(pos).setEtiq("et_" + nTabla);
		return s;
	}

	public void insertarTipoFun(int idPos, Tipo[] a, Tipo h) {
		tabla.get(idPos).setTipo(Tipo.funcion);
		if (a[0] == Tipo.vacio)
			tabla.get(idPos).setnParam(0);
		else
			tabla.get(idPos).setnParam(a.length);

		tabla.get(idPos).setTipoDeParam(a);
		tabla.get(idPos).setTipoDev(h);
	}

	public Map<Integer, Campos> getTabla() {
		return tabla;
	}

	public Campos get(int pos) {
		return tabla.get(pos);
	}

	public String toString() {
		String res = "";
		for (Campos campo : tabla.values()) {
			res += "\n* Lexema : '" + campo.getNombre() + "'\n\tAtributos: \n\t\t+tipo: '" + campo.getTipo() + "'\n ";
			if (campo.getDespl() != -1)
				res += "\t\t+despl: " + campo.getDespl();
			else {
				res += "\t\t\t+numParam: " + campo.getnParam() + "\n";
				for (int i = 0; i < campo.getnParam(); i++) {
					res += "\t\t\t +TipoParam" + (i + 1) + ": '" + campo.getTipoDeParam()[i] + "'\n\t\t\t  +ModoParam"
							+ (i + 1) + ": 1\n";
				}
				res+="\t\t\t +TipoRetorno: '"+campo.getTipoDev()+"'\n";
				res+="\t\t+EtiqFuncion: '"+campo.getEtiq()+"'";
			}

		}
		return res;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiq) {
		etiqueta = etiq;
	}
}