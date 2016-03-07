package lsi.pryv.epfl.pryvironic.structures;

/**
 * Created by Thieb on 19.02.2016.
 */
public class PHElectrode extends Electrode {
    private final static String STREAMID = "PH";
    private final static String TYPE = "note/txt";
    private final static String CONTENT = "This is a record of " + STREAMID;

    @Override
    public void save() {
        super.save(STREAMID,TYPE,CONTENT);
    }

    @Override
    public String getName() {
        return STREAMID;
    }
}