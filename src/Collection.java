import java.util.EnumSet;
import java.util.Set;

// Stores which Eeveelutions the player has caught
// EnumSet is used because it's fast and naturally ordered by enum declaration
public class Collection {

    private final Set<Eeveelution> caught = EnumSet.noneOf(Eeveelution.class);

    public void add(Eeveelution e)    { caught.add(e); }
    public boolean has(Eeveelution e) { return caught.contains(e); }
    public Set<Eeveelution> getAll()  { return caught; }
    public int count()                { return caught.size(); }
    public int total()                { return Eeveelution.values().length; }
    public boolean hasAll()           { return caught.size() == total(); }
}
