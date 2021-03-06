// Copyright 2015 ThoughtWorks, Inc.

// This file is part of getgauge/Intellij-plugin.

// getgauge/Intellij-plugin is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// getgauge/Intellij-plugin is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with getgauge/Intellij-plugin.  If not, see <http://www.gnu.org/licenses/>.

package com.thoughtworks.gauge.language.psi;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiElement;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.connection.GaugeConnection;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import com.thoughtworks.gauge.util.GaugeUtil;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

public class SpecPsiImplUtil {

    public static StepValue getStepValue(SpecStep element) {
        ASTNode step = element.getNode();
        String stepText = step.getText().trim();
        int newLineIndex = stepText.indexOf("\n");
        int endIndex = newLineIndex == -1 ? stepText.length() : newLineIndex;
        SpecTable inlineTable = element.getInlineTable();
        stepText = stepText.substring(1, endIndex).trim();
        return getStepValueFor(element, stepText, inlineTable != null);
    }

    public static StepValue getStepValueFor(PsiElement element, String stepText, Boolean hasInlineTable) {
        return getStepValueFor(GaugeUtil.moduleForPsiElement(element), element, stepText, hasInlineTable);
    }

    public static StepValue getStepValueFor(Module module, PsiElement element, String stepText, Boolean hasInlineTable) {
        GaugeService gaugeService = Gauge.getGaugeService(module, false);
        if (gaugeService == null) {
            return getDefaultStepValue(element);
        }
        GaugeConnection apiConnection = gaugeService.getGaugeConnection();
        if (apiConnection == null) {
            return getDefaultStepValue(element);
        }
        StepValue value = StepUtil.getStepValue(apiConnection, stepText, hasInlineTable);
        return value == null ? getDefaultStepValue(element) : value;
    }

    private static StepValue getDefaultStepValue(PsiElement element) {
        return new StepValue(element.getText(), element.getText(), new ArrayList<>());
    }

    public static ItemPresentation getPresentation(final SpecStepImpl element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }
}
