package com.xuannguyen.identity.dto.request.categoryrequest;

import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
    private String name;
    private boolean enable;

    private String avatarId;
}
