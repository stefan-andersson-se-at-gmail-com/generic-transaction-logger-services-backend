/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.erbjuder.logger.server.entity.interfaces;

import java.util.Set;

/**
 *
 * @author Stefan Andersson
 */
public interface LogMessageDataSelection {
    
    public Set<Class> dataFromClass();
    
}
