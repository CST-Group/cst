package br.unicamp.cst.bindings.soar;

public class SoarCommandChange {
    String productionName = null;
    double quantity = 0;
    String apply = "false";

    public void setProductionName(String productionName) {
        this.productionName = productionName;
    }

    public String getProductionName() {
        return productionName;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String isApply() {
        return apply;
    }
}
