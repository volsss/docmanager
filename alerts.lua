local supported = {
  NOTE = true,
  TIP = true,
  IMPORTANT = true,
  WARNING = true,
  CAUTION = true
}

local labels = {
  NOTE = "ЗАМЕЧАНИЕ",
  TIP = "СОВЕТ",
  IMPORTANT = "ВАЖНО",
  WARNING = "ПРЕДУПРЕЖДЕНИЕ",
  CAUTION = "ВНИМАНИЕ"
}

local function remove_prefix(inlines, prefix)
  local result = pandoc.List()
  local remaining = #prefix

  for _, inline in ipairs(inlines) do
    if remaining > 0 and inline.t == "Str" then
      local value = inline.text

      if #value <= remaining then
        remaining = remaining - #value
      else
        inline.text = value:sub(remaining + 1)
        remaining = 0
        result:insert(inline)
      end
    else
      result:insert(inline)
    end
  end

  if result[1] and
      (result[1].t == "Space" or result[1].t == "SoftBreak") then
    table.remove(result, 1)
  end

  return result
end

function BlockQuote(el)
  if #el.content == 0 or el.content[1].t ~= "Para" then
    return nil
  end

  local first = el.content[1]
  local text = pandoc.utils.stringify(first.content)
  local kind = text:match("^%[!([A-Z]+)%]")

  if not kind or not supported[kind] then
    return nil
  end

  first.content = remove_prefix(first.content, "[!" .. kind .. "]")

  table.insert(first.content, 1, pandoc.Space())
  table.insert(
    first.content,
    1,
    pandoc.Strong({ pandoc.Str((labels[kind] or kind) .. ":") })
  )

  return pandoc.Div(
    el.content,
    pandoc.Attr("", {"alert", "alert-" .. kind:lower()}, {
      ["custom-style"] = "Block Text"
    })
  )
end
