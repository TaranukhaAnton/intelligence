package ua.gov.intelligence.service.signal;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.service.filter.DoubleFilter;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.domain.TriangulationPoint;
import ua.gov.intelligence.domain.signal.DataMessage;
import ua.gov.intelligence.domain.signal.Envelope;
import ua.gov.intelligence.domain.signal.SignalMessage;
import ua.gov.intelligence.repository.TriangulationPointRepository;
import ua.gov.intelligence.service.FrequencyQueryService;
import ua.gov.intelligence.service.FrequencyService;
import ua.gov.intelligence.service.TriangulationPointService;
import ua.gov.intelligence.service.criteria.FrequencyCriteria;

@Service
public class RemoteApiServiceImpl implements RemoteApiService {

    private final Logger log = LoggerFactory.getLogger(RemoteApiService.class);

    public static final String TEST = "pKe+C6kVPQuVbWOgLzZreXj77pexfbXBUlrh2sY6Zk0=";
    public static final String STEPNO = "Btb71pNufy93e/tEZ4A9RDILd7CcdLprMB6U4Y7+KdM=";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

    private final RestTemplate restTemplate;

    private final TriangulationPointService triangulationPointService;
    private final FrequencyQueryService frequencyQueryService;
    private final FrequencyService frequencyService;

    private final TriangulationPointRepository triangulationPointRepository;

    public RemoteApiServiceImpl(
        RestTemplate restTemplate,
        TriangulationPointService triangulationPointService,
        FrequencyQueryService frequencyQueryService,
        FrequencyService frequencyService,
        TriangulationPointRepository triangulationPointRepository
    ) {
        this.restTemplate = restTemplate;
        this.triangulationPointService = triangulationPointService;
        this.frequencyQueryService = frequencyQueryService;
        this.frequencyService = frequencyService;

        this.triangulationPointRepository = triangulationPointRepository;
    }

    //            @Scheduled(fixedDelay = 3000000)
    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() {
        final List<SignalMessage> messages = getMessages();

        System.out.println("messages.size() = " + messages.size());
        for (SignalMessage message : messages) {
            final Envelope envelope = message.getEnvelope();
            if (envelope == null) {
                continue;
            }
            final DataMessage dataMessage = envelope.getDataMessage();
            if (dataMessage == null) {
                continue;
            }

            String groupId = getGroupId(message);
            System.out.println("groupId = " + groupId);

            if (STEPNO.equals(groupId)) {
                System.out.println("message = " + dataMessage.getMessage());
                processTriangulationPoint(dataMessage);
            }

            if (TEST.equals(groupId)) {
                System.out.println("message = " + dataMessage.getMessage());
                processTriangulationPoint(dataMessage);
            }
        }
    }

    private void processTriangulationPoint(DataMessage message) {
        final String text = message.getMessage();
        final String replace = text.replace("\n\n", "\n");
        final String[] split = replace.split("\\n");

        if (split.length == 3) {
            try {
                final Double f = Double.parseDouble(split[0].replace(",", ".").trim());
                final Double lat = Double.parseDouble(split[1].replace(",", ".").trim());
                final Double lon = Double.parseDouble(split[2].replace(",", ".").trim());

                final Frequency frequency = getFrequency(f).orElseGet(() -> new Frequency(f));

                TriangulationPoint p = new TriangulationPoint();
                p.setDate(ZonedDateTime.now());
                p.setFrequency(frequency);
                p.setLatitude(lat);
                p.setLongitude(lon);
                frequency.addTriangulationPoint(p);
                frequencyService.save(frequency);
            } catch (Exception e) {
                log.error("text = " + text, e);
            }
        } else {
            log.error("text = " + text);
        }
    }

    private Optional<Frequency> getFrequency(Double frequency) {
        Optional<Frequency> f;

        final FrequencyCriteria criteria = new FrequencyCriteria();
        final DoubleFilter name = new DoubleFilter();
        name.setGreaterThanOrEqual(frequency - 0.05d);
        name.setLessThanOrEqual(frequency + 0.05d);
        criteria.setName(name);
        final List<Frequency> frequencies = frequencyQueryService.findByCriteria(criteria);
        if (frequencies.size() > 1) {
            String collect = frequencies.stream().map(Frequency::getName).map(Object::toString).collect(Collectors.joining(","));
            log.error("Found more than one frequency " + frequency + " -> [" + collect + "]");
        }
        if (frequencies.isEmpty()) {
            f = Optional.empty();
        } else {
            final Frequency value = frequencies.get(0);

            final Frequency frequency1 = new Frequency();
            //todo remove this
            frequency1.setId(value.getId());
            frequency1.setName(value.getName());
            frequency1.setDescription(value.getDescription());

            f = Optional.of(frequency1);
        }
        return f;
    }

    @Override
    public List<SignalMessage> getMessages() {
        //        String url = "http://localhost:8080/v1/receive/+380957319209";
        String url = "http://localhost:8080/v1/receive/+380965127484";
        ResponseEntity<SignalMessage[]> response = restTemplate.getForEntity(url, SignalMessage[].class);
        return Arrays.asList(response.getBody());
    }

    private String getGroupId(SignalMessage message) {
        final Map<String, Object> additionalProperties = message.getEnvelope().getDataMessage().getAdditionalProperties();
        final Map<String, String> groupInfo = (Map<String, String>) additionalProperties.get("groupInfo");
        if (groupInfo != null) {
            return groupInfo.get("groupId");
        } else {
            return null;
        }
    }
}
