/**
 * @author Jaime
 * @author Xin Ru
 * @author Lucia
 */

public class Campos {
	private String nombre;
	private int despl;
	private Tipo tipo;
	private int nParam;
	private Tipo tipoDeParam[];
	private Tipo tipoDev;
	private String etiq;
	
	public Campos(String nombre,Tipo tipo, int despl) {
		this.nombre=nombre;
		this.tipo = tipo;
		this.despl = despl;
	}
	
	public Campos(String nombre, Tipo tipo, int npar, Tipo tipoDeParam[], Tipo tipoDev, String etiq) {
		this.nombre=nombre;
		this.tipo=tipo;
		this.nParam=npar;
		this.tipoDeParam=tipoDeParam;
		this.tipoDev=tipoDev;
		this.etiq=etiq;
	}
	
	public String getNombre() {
		return nombre;
	}

	public int getDespl() {
		return despl;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public int getnParam() {
		return nParam;
	}

	public Tipo[] getTipoDeParam() {
		return tipoDeParam;
	}

	public Tipo getTipoDev() {
		return tipoDev;
	}

	public String getEtiq() {
		return etiq;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDespl(int despl) {
		this.despl = despl;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public void setnParam(int nParam) {
		this.nParam = nParam;
	}

	public void setTipoDeParam(Tipo[] tipoDeParam) {
		this.tipoDeParam = tipoDeParam;
	}

	public void setTipoDev(Tipo tipoDev) {
		this.tipoDev = tipoDev;
	}

	public void setEtiq(String etiq) {
		this.etiq = etiq;
	}
	
}