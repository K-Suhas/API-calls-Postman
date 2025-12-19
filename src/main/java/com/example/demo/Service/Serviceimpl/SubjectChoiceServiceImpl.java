package com.example.demo.Service.Serviceimpl;

import com.example.demo.DTO.SubjectChoiceRequestDTO;
import com.example.demo.DTO.SubjectChoiceResponseDTO;
import com.example.demo.Domain.StudentSubjectChoiceDomain;
import com.example.demo.Repository.StudentSubjectChoiceRepository;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Service.SubjectChoiceService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class SubjectChoiceServiceImpl implements SubjectChoiceService {

    private final StudentSubjectChoiceRepository repo;
    private final SubjectRepository subjectRepo;
    private final StudentRepository studentRepo;

    public SubjectChoiceServiceImpl(StudentSubjectChoiceRepository repo,
                                    SubjectRepository subjectRepo,
                                    StudentRepository studentRepo) {
        this.repo = repo;
        this.subjectRepo = subjectRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public SubjectChoiceResponseDTO createChoice(SubjectChoiceRequestDTO req) {
        var student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!Objects.equals(student.getDepartment().getId(), req.getDepartmentId())) {
            throw new RuntimeException("Department mismatch");
        }

        // ✅ Check this FIRST and return immediately
        if (repo.existsByStudentIdAndSemester(req.getStudentId(), req.getSemester())) {
            throw new RuntimeException("Subjects already chosen for this semester");
        }

        if (req.getSubjectIds() == null || req.getSubjectIds().size() != 4) {
            throw new RuntimeException("You must choose exactly 4 subjects");
        }

        var subjects = subjectRepo.findAllById(req.getSubjectIds());
        if (subjects.size() != 4) {
            throw new RuntimeException("Invalid subject selection");
        }

        boolean valid = subjects.stream().allMatch(s ->
                Objects.equals(s.getDepartment().getId(), req.getDepartmentId()) &&
                        Objects.equals(s.getSemester(), req.getSemester())
        );
        if (!valid) {
            throw new RuntimeException("Subject does not belong to department/semester");
        }

        var choice = new StudentSubjectChoiceDomain();
        choice.setStudentId(req.getStudentId());
        choice.setDepartmentId(req.getDepartmentId());
        choice.setSemester(req.getSemester());
        choice.setSubjectIds(new ArrayList<>(req.getSubjectIds()));
        choice.setLocked(true);
        repo.save(choice);

        var resp = new SubjectChoiceResponseDTO();
        resp.setStudentId(req.getStudentId());
        resp.setSemester(req.getSemester());
        resp.setDepartmentId(req.getDepartmentId());
        resp.setSubjectIds(req.getSubjectIds());
        resp.setLocked(true);
        return resp;
    }



    @Override
    public Optional<SubjectChoiceResponseDTO> getChoice(Long studentId, Integer semester) {
        return repo.findByStudentIdAndSemester(studentId, semester)
                .map(c -> {
                    var resp = new SubjectChoiceResponseDTO();
                    resp.setStudentId(c.getStudentId());
                    resp.setSemester(c.getSemester());
                    resp.setDepartmentId(c.getDepartmentId());
                    resp.setSubjectIds(c.getSubjectIds());
                    resp.setLocked(c.isLocked());
                    return resp;
                });
    }
}
