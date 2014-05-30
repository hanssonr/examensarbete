package se.rhel.model.component;


public class TeamComponent implements IComponent, ITeam {

    private int mTeam = -1;

    public TeamComponent(int team) {
        mTeam = team;
    }

    public int getTeam() {
        return mTeam;
    }
}
