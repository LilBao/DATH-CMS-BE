package com.cms.auth.factory;

import com.cms.common.enums.AuthProviderType;
import com.cms.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ============================================================
 * FACTORY CLASS
 * ============================================================
 * AuthProviderFactory tự động thu thập tất cả AuthProvider beans
 * được đăng ký trong Spring context và map theo AuthProviderType.
 *
 * Cách hoạt động:
 *   1. Spring inject tất cả beans implement AuthProvider
 *   2. Factory tạo Map<AuthProviderType, AuthProvider>
 *   3. Khi cần, factory.getProvider(type) trả về đúng implementation
 *
 * Để thêm provider mới (ví dụ Facebook):
 *   - Tạo class FacebookAuthProvider implements AuthProvider
 *   - Annotate @Component
 *   - Factory tự động nhận diện, không cần sửa code!
 * ============================================================
 */
@Slf4j
@Component
public class AuthProviderFactory {

    private final Map<AuthProviderType, AuthProvider> providerMap;

    /**
     * Spring tự inject List<AuthProvider> chứa tất cả implementations
     */
    public AuthProviderFactory(List<AuthProvider> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(
                        AuthProvider::getType,
                        Function.identity()
                ));

        log.info("AuthProviderFactory initialized with {} providers: {}",
                providers.size(),
                providerMap.keySet()
        );
    }

    /**
     * Lấy AuthProvider theo loại
     * @param type Loại provider (LOCAL, GOOGLE, FACEBOOK)
     * @return Implementation tương ứng
     * @throws AppException nếu không tìm thấy provider
     */
    public AuthProvider getProvider(AuthProviderType type) {
        AuthProvider provider = providerMap.get(type);
        if (provider == null) {
            throw AppException.badRequest(
                    "Unsupported authentication provider: " + type
            );
        }
        return provider;
    }

    /**
     * Kiểm tra provider có tồn tại không
     */
    public boolean hasProvider(AuthProviderType type) {
        return providerMap.containsKey(type);
    }
}
