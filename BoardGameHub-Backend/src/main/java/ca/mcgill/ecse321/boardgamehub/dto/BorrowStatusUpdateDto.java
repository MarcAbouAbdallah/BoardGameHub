package ca.mcgill.ecse321.boardgamehub.dto;

import ca.mcgill.ecse321.boardgamehub.model.BorrowStatus;

public class BorrowStatusUpdateDto {

    private BorrowStatus status;

    protected BorrowStatusUpdateDto() {}

    public BorrowStatusUpdateDto(BorrowStatus status) {
        this.status = status;
    }

    public BorrowStatus getStatus(){
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }
}