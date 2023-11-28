package org.example.task3;

import java.sql.Date;

public class Posting {

    private String MatDoc;
    private int Item;
    private Date DocDate;
    private Date PstngDate;
    private String MaterialDescription;
    private int Quantity;
    private String BUn;
    private float AmountLC;
    private String Crcy;
    private String UserName;
    private Boolean AuthorizedPosting;

    public Posting(String matDoc, int item, Date docDate, Date pstngDate,
                   String materialDescription, int quantity, String BUn, float amountLC,
                   String crcy, String userName, Boolean authorizedPosting) {
        this.MatDoc = matDoc;
        this.Item = item;
        this.DocDate = docDate;
        this.PstngDate = pstngDate;
        this.MaterialDescription = materialDescription;
        this.Quantity = quantity;
        this.BUn = BUn;
        this.AmountLC = amountLC;
        this.Crcy = crcy;
        this.UserName = userName;
        this.AuthorizedPosting = authorizedPosting;
    }

    public String getMatDoc() {
        return MatDoc;
    }

    public int getItem() {
        return Item;
    }

    public Date getDocDate() {
        return DocDate;
    }

    public Date getPstngDate() {
        return PstngDate;
    }

    public String getMaterialDescription() {
        return MaterialDescription;
    }

    public int getQuantity() {
        return Quantity;
    }

    public String getBUn() {
        return BUn;
    }

    public float getAmountLC() {
        return AmountLC;
    }

    public String getCrcy() {
        return Crcy;
    }

    public String getUserName() {
        return UserName;
    }

    public Boolean getAuthorizedPosting() {
        return AuthorizedPosting;
    }

    public void setMatDoc(String matDoc) {
        MatDoc = matDoc;
    }

    public void setItem(int item) {
        Item = item;
    }

    public void setDocDate(Date docDate) {
        DocDate = docDate;
    }

    public void setPstngDate(Date pstngDate) {
        PstngDate = pstngDate;
    }

    public void setMaterialDescription(String materialDescription) {
        MaterialDescription = materialDescription;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public void setBUn(String BUn) {
        this.BUn = BUn;
    }

    public void setAmountLC(float amountLC) {
        AmountLC = amountLC;
    }

    public void setCrcy(String crcy) {
        Crcy = crcy;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setAuthorizedPosting(Boolean authorizedPosting) {
        AuthorizedPosting = authorizedPosting;
    }

    @Override
    public String toString() {
        return "posting{" +
                "MatDoc='" + MatDoc + '\'' +
                ", Item=" + Item +
                ", DocDate=" + DocDate +
                ", PstngDate=" + PstngDate +
                ", MaterialDescription='" + MaterialDescription + '\'' +
                ", Quantity=" + Quantity +
                ", BUn='" + BUn + '\'' +
                ", AmountLC=" + AmountLC +
                ", Crcy='" + Crcy + '\'' +
                ", UserName='" + UserName + '\'' +
                ", AuthorizedPosting=" + AuthorizedPosting +
                '}';
    }

}
