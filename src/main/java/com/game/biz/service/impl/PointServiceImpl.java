package com.game.biz.service.impl;

import com.game.biz.model.Resultat;
import com.game.biz.service.PointService;
import com.game.biz.model.Point;
import com.game.biz.service.ResultatService;
import com.game.repository.biz.PointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Point}.
 */
@Service
@Transactional
public class PointServiceImpl implements PointService {

    private final Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    private final PointRepository pointRepository;

    private ResultatService resultatService;

    public PointServiceImpl(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    /**
     * Save a point.
     *
     * @param point the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Point save(Point point) {
        log.debug("Request to save Point : {}", point);
        Point point_retour =  pointRepository.save(point);
        Resultat resultat = resultatService.findByUserIdAndCategorie(point.getUserId(), point.getCategorie()).orElseGet(Resultat::new);
        resultat.setCategorie(point.getCategorie());
        resultat.setUserId(point.getUserId());
        resultat.setPoints(resultat.getPoints() + point.getNbPoints());
        return point_retour;
    }

    @Override
    public void saveAll(List<Point> points) {
        points.stream().forEach(p -> save(p));
    }

    /**
     * Get all the points.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Point> findAll() {
        log.debug("Request to get all Points");
        return pointRepository.findAll();
    }


    /**
     * Get one point by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Point> findOne(Long id) {
        log.debug("Request to get Point : {}", id);
        return pointRepository.findById(id);
    }

    /**
     * Delete the point by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Point : {}", id);
        pointRepository.deleteById(id);
    }

    @Override
    public List<Point> findLast2WByUserId(Long userId) {
        LocalDate w2Ago = LocalDate.now().with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek()).atStartOfDay().minusWeeks(2).toLocalDate();

        return pointRepository.findAllByUserIdAndDateGreaterThan(userId, w2Ago);
    }
}
