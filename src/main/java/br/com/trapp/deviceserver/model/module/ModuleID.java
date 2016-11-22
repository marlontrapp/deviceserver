package br.com.trapp.deviceserver.model.module;

public class ModuleID {

    private String name;
    private String identifier;

    public ModuleID() {
	// TODO Auto-generated constructor stub
    }

    public ModuleID(String name, String identifier) {
	this.name = name;
	this.identifier = identifier;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getIdentifier() {
	return identifier;
    }

    public void setIdentifier(String identifier) {
	this.identifier = identifier;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	ModuleID other = (ModuleID) obj;
	if (identifier == null) {
	    if (other.identifier != null)
		return false;
	} else if (!identifier.equals(other.identifier))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }

}
