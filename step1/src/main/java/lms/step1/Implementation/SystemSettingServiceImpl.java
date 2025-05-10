package lms.step1.Implementation;

import lms.step1.Model.SystemSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lms.step1.DTO.SystemSettingDTO;
import lms.step1.Service.SystemSettingService;
import lms.step1.Repository.SystemSettingRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository repository;

    @Override
    public void createSetting(SystemSettingDTO dto) {
        log.info("⚙️ Attempting to create new system setting: key={}, value={}", dto.getKey(), dto.getValue());

        if (repository.existsByKey(dto.getKey())) {
            log.warn("❌ Setting with key '{}' already exists.", dto.getKey());
            throw new RuntimeException("Setting with this key already exists.");
        }

        SystemSetting setting = SystemSetting.builder()
                .key(dto.getKey())
                .value(dto.getValue())
                .build();

        repository.save(setting);
        log.info("✅ System setting saved successfully: key={}", dto.getKey());
    }
}
