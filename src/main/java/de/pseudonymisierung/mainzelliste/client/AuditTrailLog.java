package de.pseudonymisierung.mainzelliste.client;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class represents the information required for audittrail logging
 */
public class AuditTrailLog {

  private String username;
  private String remoteSystem;
  private String reasonForChange;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getRemoteSystem() {
    return remoteSystem;
  }

  public void setRemoteSystem(String remoteSystem) {
    this.remoteSystem = remoteSystem;
  }

  public String getReasonForChange() {
    return reasonForChange;
  }

  public void setReasonForChange(String reasonForChange) {
    this.reasonForChange = reasonForChange;
  }

  /**
   * This method converts the instance data to JSON
   * @return {@link JSONObject}, which holds the data of this instance for JSON conversion
   * @throws JSONException
   */
  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("username", this.getUsername());
    jsonObject.put("remoteSystem", this.getRemoteSystem());
    jsonObject.put("reasonForChange", this.getReasonForChange());
    return jsonObject;
  }
}
