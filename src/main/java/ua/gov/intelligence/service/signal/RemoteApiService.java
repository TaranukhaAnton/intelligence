package ua.gov.intelligence.service.signal;

import java.util.List;
import ua.gov.intelligence.domain.signal.SignalMessage;

public interface RemoteApiService {
    List<SignalMessage> getMessages();
}
