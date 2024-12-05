package com.beltra.sma.service;

import com.beltra.sma.model.Prestazione;
import com.beltra.sma.repository.PrestazioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestazioneServiceImpl implements PrestazioneService {

    private final PrestazioneRepository prestazioneRepository;

    public PrestazioneServiceImpl(PrestazioneRepository prestazioneRepository) {
        this.prestazioneRepository = prestazioneRepository;
    }


    @Override
    public List<Prestazione> getAllPrestazioni() {
        return prestazioneRepository.findAll();
    }

    @Override
    public List<Prestazione> getAllPrestazioniDisponibili() {
        return prestazioneRepository
                .findAll()
                .stream()
                .filter( prest -> !prest.getDeleted() )
                .toList();
    }
}
