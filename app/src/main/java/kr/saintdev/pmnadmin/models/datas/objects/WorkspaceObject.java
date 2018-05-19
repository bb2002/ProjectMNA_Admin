package kr.saintdev.pmnadmin.models.datas.objects;

public class WorkspaceObject {
    private String workspaceName = null;
    private String workspaceUUID = null;
    private String workspaceCreated = null;

    public WorkspaceObject(String workspaceName, String workspaceUUID, String workspaceCreated) {
        this.workspaceName = workspaceName;
        this.workspaceUUID = workspaceUUID;
        this.workspaceCreated = workspaceCreated;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getWorkspaceUUID() {
        return workspaceUUID;
    }

    public String getWorkspaceCreated() {
        return workspaceCreated;
    }
}
