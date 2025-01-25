package com.beltra.sma.service;

import com.beltra.sma.model.Anagrafica;
import com.beltra.sma.model.Medico;
import com.beltra.sma.repository.AnagraficaRepository;
import com.beltra.sma.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MedicoServiceImpl implements MedicoService {



    private final MedicoRepository medicoRepository;
    private final AnagraficaRepository anagraficaRepository;

    public MedicoServiceImpl(MedicoRepository medicoRepository, AnagraficaRepository anagraficaRepository) {
        this.medicoRepository = medicoRepository;
        this.anagraficaRepository = anagraficaRepository;
    }

    @Override
    public List<Medico> getAllMedici() {
        return medicoRepository.findAll();
    }

    @Override
    public Medico getMedicoByAnagrafica(Anagrafica anagrafica) {
        return medicoRepository.findByAnagrafica( anagrafica );
    }

    public Medico getFirstMedico() {return getAllMedici().stream().min(Comparator.comparing(Medico::getIdAnagrafica)).get();}

    public Medico getMedicoByIdAnagrafica(Long idAnagrafica) {
        return getAllMedici()
                .stream()
                .filter( med -> med.getAnagrafica().getIdAnagrafica().equals(idAnagrafica) )
                .findFirst()
                .orElse(null);
    }
}
