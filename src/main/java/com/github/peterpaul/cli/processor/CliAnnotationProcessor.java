package com.github.peterpaul.cli.processor;

import com.github.peterpaul.cli.Cli;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class CliAnnotationProcessor extends AbstractProcessor {
    public CliAnnotationProcessor() {
        super();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new LinkedHashSet<>(Arrays.asList(
                Cli.Argument.class.getCanonicalName(),
                Cli.Command.class.getCanonicalName(),
                Cli.Option.class.getCanonicalName(),
                Cli.Run.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        roundEnvironment.getElementsAnnotatedWith(Cli.Command.class)
                .stream()
                .filter((element) -> element.getKind() == ElementKind.CLASS);
        return true;
    }

    private void error(Element e, String msg, Object... args) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
