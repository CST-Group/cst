package br.unicamp.cst.bindings.soar;

public class SoarCommandNested {
    String productionName = null;
    double quantity = 0;
    String apply = "false";
    SoarCommandChange nestedClass = null;

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

    public SoarCommandChange getNestedClass() {
        return nestedClass;
    }

    public void setNestedClass(SoarCommandChange nestedClass) {
        this.nestedClass = nestedClass;
    }
}
