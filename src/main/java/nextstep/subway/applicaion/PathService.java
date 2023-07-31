package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public PathResponse getPath(Long sourceId, Long targetId) {
        List<Line> lines = lineRepository.findAll();
        List<Long> shortestPath = new Path(lines).getShortestPath(sourceId, targetId);

        List<StationResponse> stationResponses = convertToStationResponses(shortestPath);

        return new PathResponse(stationResponses);
    }

    private List<StationResponse> convertToStationResponses(List<Long> shortestPath) {
        List<StationResponse> stationResponses = shortestPath.stream()
                .map(stationId -> {
                    Station station = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);

                    return new StationResponse(station.getId(), station.getName());
                }).collect(Collectors.toList());
        return stationResponses;
    }
}
