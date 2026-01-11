import re
import sqlite3


def parse_terrain_cfg_to_sqlite(cfg_content, db_path="terrain.db"):
    """将地形配置文件解析并存入SQLite数据库"""

    # 移除文件头注释
    content = cfg_content
    if "[file content begin]" in content:
        content = content.split("[file content begin]\n", 1)[1]
    if "[file content end]" in content:
        content = content.rsplit("[file content end]", 1)[0]

    # 连接SQLite数据库
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # 创建地形类型表
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS terrain_types (
        id TEXT PRIMARY KEY,
        symbol_image TEXT,
        editor_image TEXT,
        icon_image TEXT,
        name TEXT,
        editor_name TEXT,
        string TEXT,
        aliasof TEXT,
        submerge REAL,
        editor_group TEXT,
        help_topic_text TEXT,
        light INTEGER,
        max_light INTEGER,
        heals INTEGER,
        gives_income BOOLEAN,
        default_base TEXT,
        mvt_alias TEXT,
        def_alias TEXT,
        unit_height_adjust INTEGER,
        recruit_from BOOLEAN,
        recruit_onto BOOLEAN,
        hidden BOOLEAN,
        hide_help BOOLEAN,
        hide_if_impassable BOOLEAN
    )
    ''')

    # 创建编辑器组表（多对多关系）
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS editor_groups (
        terrain_id TEXT,
        group_name TEXT,
        FOREIGN KEY (terrain_id) REFERENCES terrain_types(id),
        PRIMARY KEY (terrain_id, group_name)
    )
    ''')

    # 正则匹配所有[terrain_type]块
    terrain_pattern = r'\[terrain_type\](.*?)\[/terrain_type\]'
    terrain_blocks = re.findall(terrain_pattern, content, re.DOTALL | re.IGNORECASE)

    print(f"找到 {len(terrain_blocks)} 个地形类型")

    for block in terrain_blocks:
        terrain_data = {}

        # 解析块内的所有字段
        lines = block.strip().split('\n')

        current_key = None
        current_value = []

        for line in lines:
            line = line.strip()

            # 跳过空行和纯注释行
            if not line or line.startswith('#'):
                if current_key and current_value:
                    # 保存当前字段的值
                    terrain_data[current_key] = '\n'.join(current_value).strip()
                    current_key = None
                    current_value = []
                continue

            # 检查是否是新的键值对
            match = re.match(r'([a-zA-Z_]+)\s*=\s*(.*)', line)
            if match:
                # 如果有正在收集的字段，先保存
                if current_key and current_value:
                    terrain_data[current_key] = '\n'.join(current_value).strip()

                key = match.group(1)
                value = match.group(2).strip()

                # 检查值是否以 _ " 开头（可能是多行字符串）
                if value.startswith('_ "') and value.count('"') == 1:
                    # 开始收集多行字符串
                    current_key = key
                    current_value = [value]
                else:
                    # 单行值
                    # 清理翻译标记
                    if value.startswith('_ "'):
                        value = re.sub(r'^_\s*"|"$', '', value)
                    terrain_data[key] = value
                    current_key = None
                    current_value = []
            elif current_key:
                # 继续收集多行字符串
                current_value.append(line)
            else:
                # 可能是没有等号的特殊行，跳过
                pass

        # 保存最后一个字段
        if current_key and current_value:
            terrain_data[current_key] = '\n'.join(current_value).strip()

        # 清理翻译标记
        for key in ['name', 'editor_name', 'help_topic_text']:
            if key in terrain_data:
                value = terrain_data[key]
                if value.startswith('_ "'):
                    terrain_data[key] = re.sub(r'^_\s*"|"$', '', value)

        # 提取ID（必需字段）
        if 'id' not in terrain_data:
            print(f"警告：跳过没有id的地形类型")
            continue

        # 准备插入数据库的值
        values = (
            terrain_data.get('id', ''),
            terrain_data.get('symbol_image', ''),
            terrain_data.get('editor_image', ''),
            terrain_data.get('icon_image', ''),
            terrain_data.get('name', ''),
            terrain_data.get('editor_name', ''),
            terrain_data.get('string', ''),
            terrain_data.get('aliasof', ''),
            float(terrain_data.get('submerge', 0)) if terrain_data.get('submerge') else None,
            terrain_data.get('editor_group', ''),
            terrain_data.get('help_topic_text', ''),
            int(terrain_data.get('light', 0)) if terrain_data.get('light') else None,
            int(terrain_data.get('max_light', 0)) if terrain_data.get('max_light') else None,
            int(terrain_data.get('heals', 0)) if terrain_data.get('heals') else None,
            1 if terrain_data.get('gives_income') == 'yes' else 0,
            terrain_data.get('default_base', ''),
            terrain_data.get('mvt_alias', ''),
            terrain_data.get('def_alias', ''),
            int(terrain_data.get('unit_height_adjust', 0)) if terrain_data.get('unit_height_adjust') else None,
            1 if terrain_data.get('recruit_from') == 'yes' else 0,
            1 if terrain_data.get('recruit_onto') == 'yes' else 0,
            1 if terrain_data.get('hidden') == 'yes' else 0,
            1 if terrain_data.get('hide_help') == 'yes' else 0,
            1 if terrain_data.get('hide_if_impassable') == 'yes' else 0
        )

        # 插入地形类型数据
        cursor.execute('''
        INSERT OR REPLACE INTO terrain_types VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 
                                                     ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 
                                                     ?, ?, ?, ?)
        ''', values)

        # 处理编辑器组（可能有多个，用逗号分隔）
        editor_groups = terrain_data.get('editor_group', '')
        if editor_groups:
            groups = [g.strip() for g in editor_groups.split(',')]
            for group in groups:
                if group:  # 跳过空组
                    cursor.execute(
                        'INSERT OR REPLACE INTO editor_groups VALUES (?, ?)',
                        (terrain_data['id'], group)
                    )

    # 提交事务
    conn.commit()

    # 打印统计信息
    cursor.execute('SELECT COUNT(*) FROM terrain_types')
    count = cursor.fetchone()[0]
    print(f"成功导入 {count} 个地形类型到数据库 {db_path}")

    # 关闭连接
    conn.close()


# 读取文件内容并解析
file_content = """[file name]: terrain.cfg
[file content begin]
#textdomain wesnoth-lib
# Terrain configuration file. Defines how the terrain _work_ in the game. How
# the terrains _look_ is defined in terrain_graphics.cfg. The background color
# of terrain type icons is determined by the terrain type color ranges in
# ./team-colors.cfg.

# NOTE: terrain id's are used implicitly by the in-game help:
# each "[terrain_type] id=some_id" corresponds to "[section] id=terrain_some_id"
# or "[topic] id=terrain_some_id" identifying its description in [help].
# It also corresponds to [color_range] id=some_id in team-colors.cfg.

# NOTE: this list is sorted to group things comprehensibly in the editor
# NOTE: this data is parsed by data/tools/terrain2wiki.py to generate
# the TerrainCodeTableWML wiki page.

#
#    ## Water ##
#

[terrain_type]
    symbol_image=water/ocean-grey-tile
    id=deep_water_gray
    name= _ "Deep Water"
    editor_name= _ "Gray Deep Water"
    string=Wog
    aliasof=Wdt
    submerge=0.5
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/ocean-tile
    id=deep_water_medium
    name= _ "Deep Water"
    editor_name= _ "Medium Deep Water"
    string=Wo
    aliasof=Wdt
    submerge=0.5
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/ocean-tropical-tile
    id=deep_water_tropical
    name= _ "Deep Water"
    editor_name= _ "Tropical Deep Water"
    string=Wot
    aliasof=Wdt
    submerge=0.5
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/coast-grey-tile
    id=gray_tropical_water
    name= _ "Shallow Water"
    editor_name= _ "Gray Shallow Water"
    string=Wwg
    aliasof=Wst
    submerge=0.4
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/coast-tile
    id=medium_shallow_water
    name= _ "Shallow Water"
    editor_name= _ "Medium Shallow Water"
    string=Ww
    aliasof=Wst
    submerge=0.4
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/coast-tropical-tile
    id=tropical_water
    name= _ "Shallow Water"
    editor_name= _ "Tropical Shallow Water"
    string=Wwt
    aliasof=Wst
    submerge=0.4
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/ford-tile
    id=ford
    name= _ "Ford"
    editor_name= _ "Ford"
    string=Wwf
    aliasof=Gt, Wst
    submerge=0.3
    editor_group=water
    help_topic_text= _ "When a river happens to be extremely shallow, passing over it is a trivial matter for land based units. Moreover, any creature best adapted to swimming has full mobility even at such places in the river. As far as gameplay is concerned, a river ford is treated as either grassland or shallow water, choosing whichever one offers the best defensive and movement bonuses for the unit on it."
[/terrain_type]

[terrain_type]
    symbol_image=water/reef-gray-tile
    id=gray_reef
    name= _ "Coastal Reef"
    editor_name= _ "Gray Coastal Reef"
    string=Wwrg
    aliasof=Wrt
    submerge=0.3
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/reef-tile
    id=medium_reef
    name= _ "Coastal Reef"
    editor_name= _ "Medium Coastal Reef"
    aliasof=Wrt
    string=Wwr
    submerge=0.3
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/reef-tropical-tile
    id=tropical_reef
    name= _ "Coastal Reef"
    editor_name= _ "Tropical Coastal Reef"
    string=Wwrt
    aliasof=Wrt
    submerge=0.3
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=water/seaweed/kelp-tile
    id=sea_kelp
    name= _ "Swamp"
    editor_name= _ "Kelp Forest"
    string=^Wkf
    aliasof=_bas, St
    submerge=0.45
    default_base=Ww
    mvt_alias=-,_bas,St
    editor_group=water
[/terrain_type]

#    ## Swamp ##

[terrain_type]
    symbol_image=swamp/water-tile
    id=swamp_water_reed
    name= _ "Swamp"
    editor_name= _ "Swamp Water Reed"
    aliasof=St
    string=Ss
    submerge=0.4
    editor_group=water
[/terrain_type]

[terrain_type]
    symbol_image=swamp/mud-tile
    id=quagmire
    name= _ "Swamp"
    editor_name= _ "Muddy Quagmire"
    string=Sm
    aliasof=St
    submerge=0.4
    editor_group=water
[/terrain_type]
"""

# 运行解析器
parse_terrain_cfg_to_sqlite(file_content)

# 测试查询
conn = sqlite3.connect("terrain.db")
cursor = conn.cursor()

# 查看表结构
print("\n=== 表结构 ===")
cursor.execute("PRAGMA table_info(terrain_types)")
columns = cursor.fetchall()
print("terrain_types 表的列：")
for col in columns:
    print(f"  {col[1]} ({col[2]})")

print("\n=== 前5个地形类型 ===")
cursor.execute("SELECT id, name, string, editor_group FROM terrain_types LIMIT 5")
for row in cursor.fetchall():
    print(f"ID: {row[0]}, 名称: {row[1]}, 代码: {row[2]}, 组: {row[3]}")

print("\n=== 编辑器组统计 ===")
cursor.execute("SELECT group_name, COUNT(*) FROM editor_groups GROUP BY group_name")
for row in cursor.fetchall():
    print(f"组: {row[0]}, 数量: {row[1]}")

conn.close()