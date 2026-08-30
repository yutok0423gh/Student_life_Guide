from __future__ import annotations

import csv
import json
import re
import sys
from pathlib import Path


SOURCE_URL = "https://www.moe.gov.cn/jyb_xxgk/s5743/s5744/202606/t20260618_1441074.html"
UPDATED_AT = "2026-06-17"
PUBLISHED_AT = "2026-06-18"
SECTION_PATTERN = re.compile(r"(.+?)（(\d+)所）")


def main() -> None:
    if len(sys.argv) != 3:
        raise SystemExit("用法：generate-school-catalog.py <教育部CSV> <输出JSON>")

    source_path = Path(sys.argv[1])
    output_path = Path(sys.argv[2])
    schools: list[dict[str, object]] = []
    province = ""
    expected_total = 0

    with source_path.open("r", encoding="utf-8-sig", newline="") as source_file:
        for raw_row in csv.reader(source_file):
            row = (raw_row + [""] * 7)[:7]
            first_cell = row[0].strip()
            section = SECTION_PATTERN.fullmatch(first_cell)
            if section:
                province = section.group(1)
                expected_total += int(section.group(2))
                continue
            if not first_cell.isdigit():
                continue
            if not province:
                raise ValueError(f"学校 {row[1]} 缺少省级分组")

            code = row[2].strip()
            name = row[1].strip()
            if not code or not name:
                raise ValueError(f"第 {first_cell} 行缺少学校标识码或名称")
            schools.append(
                {
                    "code": code,
                    "name": name,
                    "province": province,
                    "city": row[4].strip(),
                    "level": row[5].strip(),
                    "isPrivate": row[6].strip() == "民办",
                }
            )

    if len(schools) != expected_total:
        raise ValueError(f"学校数量不一致：解析 {len(schools)} 所，分组声明 {expected_total} 所")
    if len({school["code"] for school in schools}) != len(schools):
        raise ValueError("学校标识码存在重复")
    if len({school["name"] for school in schools}) != len(schools):
        raise ValueError("学校名称存在重复，搜索选择需要额外消歧")

    payload = {
        "version": 1,
        "updatedAt": UPDATED_AT,
        "publishedAt": PUBLISHED_AT,
        "sourceName": "中华人民共和国教育部",
        "sourceTitle": "全国高等学校名单",
        "sourceUrl": SOURCE_URL,
        "scope": "全国普通高等学校（不含港澳台）",
        "schools": schools,
    }
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="\n") as output_file:
        json.dump(payload, output_file, ensure_ascii=False, separators=(",", ":"))
        output_file.write("\n")

    print(f"已生成 {len(schools)} 所学校：{output_path}")


if __name__ == "__main__":
    main()
