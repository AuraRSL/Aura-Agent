package AUR.util.ambulance.Information;

import rescuecore2.standard.entities.AmbulanceTeam;

/**
 *
 * @author armanaxh - 2018
 */

public class AmbulanceInfo {
    public AmbulanceTeam me;
    public CivilianInfo workOnIt = null;
    public BuildingInfo searchTarget = null;

    public AmbulanceInfo(AmbulanceTeam ambo) {
        this.me = ambo;
    }
}
