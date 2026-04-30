public class Organisation {
    private int orgId;
    private String navn;

    public Organisation(int orgId, String navn) {
        this.orgId = orgId;
        this.navn = navn;
    }

    public int getOrgId() {
        return orgId;
    }

    public String getNavn() {
        return navn;
    }
}
