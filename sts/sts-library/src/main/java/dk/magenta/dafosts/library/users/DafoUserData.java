package dk.magenta.dafosts.library.users;

import java.util.Collection;

/**
 * Created by jubk on 03-05-2017.
 */
public interface DafoUserData {

    public String getUsername();

    public int getAccessAccountId();

    public String getOnBehalfOf();

    public Collection<String> getUserProfiles();
}
