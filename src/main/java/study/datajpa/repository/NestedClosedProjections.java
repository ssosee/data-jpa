package study.datajpa.repository;

public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    /**
     * TeamInfo는 최적화 안됨
     */
    interface TeamInfo {
        String getName();
    }
}
