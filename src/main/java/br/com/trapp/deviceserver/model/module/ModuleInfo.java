package br.com.trapp.deviceserver.model.module;

public class ModuleInfo {

    private String name;
    private String idendifier;
    private String version;

    public ModuleInfo(String name, String idendifier, String version) {
	this.name = name;
	this.idendifier = idendifier;
	this.version = version;
    }

    public ModuleInfo() {
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getIdendifier() {
	return idendifier;
    }

    public void setIdendifier(String idendifier) {
	this.idendifier = idendifier;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((idendifier == null) ? 0 : idendifier.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ModuleInfo other = (ModuleInfo) obj;
	if (idendifier == null) {
	    if (other.idendifier != null)
		return false;
	} else if (!idendifier.equals(other.idendifier))
	    return false;
	return true;
    }

}
