package io.github.lv.tileMap

/**
 * 表示 Wesnoth 的一个 [terrain_type] 定义（来自 terrain.cfg，WML 格式）。
 *
 * 说明：
 * - 大多数字段是可选的，因为不同地形只会定义其中一部分。
 * - 如果你不想用任何解析库，可以全部先用 String 存，后续按需要再解析。
 */
data class WesnothTerrainType(
    // ===== 基本身份 / 显示相关 =====
    val id: String? = null,
    // 不可翻译的地形 ID，用于 WML 内部逻辑引用

    val name: String? = null,
    // 地形名称（可翻译），游戏和编辑器中显示的名字

    val description: String? = null,
    // 地形的详细描述（可翻译），若未提供通常回退为 name

    val editorName: String? = null,
    // 编辑器中显示的名称（editor_name），用于更细的分类显示

    val helpTopicText: String? = null,
    // 帮助系统中的详细说明文本（help_topic_text）

    // ===== 地图编码 =====
    val stringCode: String? = null,
    // 地形在地图文件中使用的字符串编码（terrain code）

    // ===== 图像资源 =====
    val symbolImage: String? = null,
    // 小地图（minimap）中使用的图像

    val editorImage: String? = null,
    // 地图编辑器调色板中使用的图像（缺省可用 symbol_image）

    val iconImage: String? = null,
    // 帮助界面 / 侧边栏中用于显示防御与移动信息的图标

    // ===== 编辑器分组 =====
    val editorGroup: String? = null,
    // 编辑器中的分组 ID（逗号分隔），决定地形在调色板中的分类

    // ===== 可见性控制 =====
    val hidden: Boolean? = null,
    // 是否在编辑器调色板中隐藏该地形（hidden=yes）

    val hideHelp: Boolean? = null,
    // 是否在地形帮助浏览器中隐藏该地形（hide_help=yes）

    val hideIfImpassable: Boolean? = null,
    // 若某单位无法进入该地形，则在该单位的地形摘要中隐藏

    // ===== 招募 / 收入相关 =====
    val givesIncome: Boolean? = null,
    // 是否像村庄一样提供每回合收入（通常与占领标记配合）

    val recruitOnto: Boolean? = null,
    // 是否允许将新招募/召回的单位直接放到该地形上

    val recruitFrom: Boolean? = null,
    // 是否允许站在该地形上的单位进行招募

    // ===== 治疗相关 =====
    val heals: Int? = null,
    // 每回合为站在此地形上的单位恢复的生命值（非 0 时可解毒）

    // ===== 光照系统 =====
    val light: Int? = null,
    // 该地形对局部光照的修正值（影响昼夜阵营加成）

    val maxLight: Int? = null,
    // 允许的最大局部光照上限

    val minLight: Int? = null,
    // 允许的最小局部光照下限

    // ===== 移动 / 防御别名系统 =====
    val aliasOf: String? = null,
    // 地形别名定义（aliasof），用于同时影响移动和防御计算
    // 支持逗号分隔与 + / - 规则（取最好或最差）

    val defAlias: String? = null,
    // 防御别名（def_alias），仅影响防御计算，优先于 aliasof

    val mvtAlias: String? = null,
    // 移动别名（mvt_alias），仅影响移动消耗，优先于 aliasof

    val visionAlias: String? = null,
    // 视野别名（vision_alias），当前版本基本无实际效果

    // ===== 覆盖层（overlay）相关 =====
    val defaultBase: String? = null,
    // 仅作为 overlay 使用的地形，其默认搭配的基础地形

    // ===== 收入地形的描述文本 =====
    val incomeDescription: String? = null,
    // 通用的收入描述文本

    val incomeDescriptionOwn: String? = null,
    // 被自己阵营占领时的收入描述

    val incomeDescriptionAlly: String? = null,
    // 被盟友占领时的收入描述

    val incomeDescriptionEnemy: String? = null,
    // 被敌方占领时的收入描述

    // ===== 单位渲染微调 =====
    val unitHeightAdjust: Int? = null,
    // 单位站在该地形上时，单位贴图的上下偏移量

    val submerge: Double? = null
    // 单位被“淹没”的比例（0~1），用于水面/浅水等效果
)
