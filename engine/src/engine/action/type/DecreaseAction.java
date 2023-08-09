package engine.action.type;
import engine.action.AbstractAction;
import engine.action.FunctionHelper;
import engine.execution.context.Context;
import engine.property.PropertyInstance;
import engine.property.type.Type;

public class DecreaseAction extends AbstractAction {

    private final String property;
    private String byExpression;

    public DecreaseAction(String entityName, String property, String actionType, String byExpression) {
        super(entityName, actionType);
        this.property = property;
        this.byExpression = byExpression;
    }

    @Override
    public void invoke(Context context) throws RuntimeException{
        try {
            Object value = FunctionHelper.getValueToInvoke(byExpression, context, property);
            decreasePropertyValWithVal(context.getPrimaryEntityInstance().getPropertyInstanceByName(property), value);
        }
        catch (RuntimeException e){
            throw new RuntimeException("Decrease action on Entity: " + entityName + " on property: " + property +  " failed \n"
                    +  e.getMessage());
        }
    }

    private void decreasePropertyValWithVal(PropertyInstance prop, Object increaseVal){
        Type propType = prop.getType();
        Object propVal = prop.getVal();

        switch (propType){
            case DECIMAL:
                Integer int1 = Type.DECIMAL.convert(propVal);
                Integer int2 = Type.DECIMAL.convert(increaseVal);
                prop.setVal( int1 - int2);
                break;
            case FLOAT:
                Float float1 = Type.FLOAT.convert(propVal);
                Float float2 = Type.FLOAT.convert(increaseVal);
                prop.setVal( float1 - float2);
                break;
        }
    }

}
