package aLex;

public class MT_AFD {
	//Estado en el que estamos
	static int estado;
	//Accion semantica que estamos ejecutando
	static String accion;

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
	
	public void transicion (int estado, char car) {
		boolean trans = false;
		switch (estado) {
		//Primero definimos los estados
		case 0:
			if (esDig(car)) {
				estado = 1;
				accion = "X";
				trans = true;
			} else if (esMin(car)) {
				estado = 6;
				accion = "Y";
				trans = true;
			} else if (esDel(car)) {
				trans = true;
			}
			if (!trans) {
				switch(car) {
				case '/':
					estado = 7; 
					accion = "LEER";
					trans = true;
					break;
				case '"':
					estado = 5; 
					accion = "LEER";
					trans = true;
					break;
				case '(':
					estado = 18; 
					accion = "C";
					trans = true;
					break;
				case ')':
					estado = 17; 
					accion = "D";
					trans = true;
					break;
				case '{':
					estado = 15; 
					accion = "E";
					trans = true;
					break;	
				case '}':
					estado = 14; 
					accion = "F";
					trans = true;
					break;
				case '=':
					estado = 16; 
					accion = "B";
					trans = true;
					break;
				case '+':
					estado = 11; 
					accion = "A";
					trans = true;
					break;
				case '-':
					estado = 2; 
					accion = "LEER";
					trans = true;
					break;
				case '<':
					estado = 19; 
					accion = "G";
					trans = true;
					break;
				case '>':
					estado = 20; 
					accion = "H";
					trans = true;
					break;
				case '&':
					estado = 3; 
					accion = "LEER";
					trans = true;
					break;
				case '|':
					estado = 4; 
					accion = "LEER";
					trans = true;
					break;
				case ',':
					estado = 26; 
					accion = "S";
					trans = true;
					break;
				case ';':
					estado = 25; 
					accion = "R";
					trans = true;
					break;
					
				}	
			}
			if(!trans) {
				//Lanza mensaje de error.
				estado = -1;
			}
			break;
		case 1:
			if(esDig(car)){
			    estado = 1;
			    accion = "M";
			} else {
			    estado = 10;
			    accion = "N";
			}
			break;
		case 2:
			if(car=='='){
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
				//Lanza mensaje de error
				estado = -1;
			}
			break;
		case 4:
			if (car == '|') {
				estado = 22;
				accion = "J";
			} else {
				//Lanza mensaje de error
				estado = -1;
			}
			break;
		case 5:
			accion = "LEER";
			break;
		case 6:
			if (esDig(car) || esMin(car) || esMay(car) || car == '_') {
				estado = 6;
				accion ="CONC";
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
				//Lanza mensaje de error
				estado = -1;
			}
			break;
		case 8:
			if(car=='*'){
			    estado = 9;
			    accion = "LEER";
			} else{
			    estado = 8;
			    accion = "LEER";
			}
			break;
		case 9:
			if(car=='*'){
			    estado = 9;
			    accion = "LEER";
			}
			else if(car == '/'){
			    estado = 0;
			    accion = "LEER";
			}
			else{
			    estado = 8;
			    accion = "LEER";
			}
			break;
			
		}
	}
	
	private boolean esDig (char car) {
		return ((int) car >=48 && (int) car <= 57);
	}
	private boolean esMin (char car) {
		return ((int) car >=97 && (int) car <= 122) 
				||  car == 'é'
				|| ((int) car >=160 && (int) car <= 164)
				|| (int) car == 'ü';
	}
	private boolean esMay (char car) {
		return ((int) car >=65 && (int) car <= 90) 
				|| car == 'Á'
				|| car == 'É'
				|| car == 'Í'
				|| car == 'Ó'
				|| car == 'Ú'
				|| car == 'Ü';
	}
	private boolean esDel (char car) {
		return car == '\t' || car == ' ' || car == '\n'	;
	}
	

}