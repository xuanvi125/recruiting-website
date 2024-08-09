package com.bugboo.CareerConnect.event;

import com.bugboo.CareerConnect.domain.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobCreatedEvent {
    private Job job;
}
