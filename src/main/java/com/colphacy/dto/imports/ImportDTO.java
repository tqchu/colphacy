package com.colphacy.dto.imports;

import com.colphacy.dto.branch.BranchSimpleDTO;
import com.colphacy.dto.provider.ProviderSimpleDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ImportDTO {
    private Long id;

    @NotNull
    private BranchSimpleDTO branch;

    @NotNull(message = "Phải có số hóa đơn")
    private String invoiceNumber;
    @NotNull(message = "Phải có thông tin về thời gian nhập hàng")
    private ZonedDateTime importTime;

    @NotNull
    private ProviderSimpleDTO provider;

    @NotNull(message = "Phải có danh sách các sản phẩm")
    @Size(min = 1)
    private List<@Valid @NotNull ImportDetailDTO> importDetails;
}
