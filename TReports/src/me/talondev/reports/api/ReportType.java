package me.talondev.reports.api;

public enum ReportType {
  
  HACK,
  OFENSAS;
  
  public String getReadable() {
    return name().toUpperCase().substring(0, 1) + name().toLowerCase().substring(1);
  }
  
  public static ReportType getFromName(String name) {
    for (ReportType type : values()) {
      if (type.name().equalsIgnoreCase(name)) {
        return type;
      }
    }
    
    return null;
  }
}
