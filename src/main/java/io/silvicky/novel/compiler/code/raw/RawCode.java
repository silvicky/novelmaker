package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;

import java.util.List;

public interface RawCode extends Code
{
    List<Code> analyze();
}
