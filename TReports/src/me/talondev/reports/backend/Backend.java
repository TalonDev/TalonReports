package me.talondev.reports.backend;

import java.util.Collection;
import java.util.UUID;

import me.talondev.reports.api.Report;

public interface Backend {
  
  public void saveSubReports(Report report);

  public Report createReport(UUID uuid, String playerName);
  
  public boolean removeReportById(UUID uuid);
  
  public Report getReportById(UUID uuid);
  
  public Collection<Report> listReports();
}
