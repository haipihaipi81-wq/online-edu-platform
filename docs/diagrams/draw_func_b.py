import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

plt.rcParams['font.sans-serif'] = ['SimSun', 'STSong', 'Arial Unicode MS', 'Heiti SC']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(10, 6))
ax.set_xlim(0, 10)
ax.set_ylim(0, 6)
ax.axis('off')

def draw_box(ax, x, y, w, h, text, bold=False, lw=1.2):
    rect = mpatches.FancyBboxPatch(
        (x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.15",
        facecolor='white', edgecolor='black', linewidth=lw
    )
    ax.add_patch(rect)
    fs = 8.5
    wt = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fs, fontweight=wt, color='black')

def v_line(ax, x, y1, y2):
    ax.plot([x, x], [y1, y2], color='black', lw=0.8)

def h_line(ax, x1, x2, y):
    ax.plot([x1, x2], [y, y], color='black', lw=0.8)

# ===== 顶部：系统总称 =====
draw_box(ax, 5.0, 5.55, 4.0, 0.55, '在线教育平台', bold=True, lw=1.5)

# 竖线下来
v_line(ax, 5.0, 5.27, 4.95)

# 横线分叉
h_line(ax, 2.6, 7.4, 4.95)

# 两个竖线到两个模块
mod_x = [2.6, 7.4]
for mx in mod_x:
    v_line(ax, mx, 4.95, 4.55)

# ===== 两大模块 =====
draw_box(ax, 2.6, 4.2, 3.8, 0.55, '直播教学模块', bold=True)
draw_box(ax, 7.4, 4.2, 3.8, 0.55, '作业与考试模块', bold=True)

# 竖线到子功能
for mx in mod_x:
    v_line(ax, mx, 3.92, 3.6)

# 横线
h_line(ax, 2.6, 7.4, 3.6)

# ===== 直播教学子功能 =====
live_children = [
    '直播间创建\n与管理',
    '推拉流地址\n配置',
    'WebSocket\n实时弹幕',
    '录制回放\n管理',
]
live_n = len(live_children)
live_spacing = 3.6 / (live_n + 1)
for i, child in enumerate(live_children):
    cx = 2.6 - 1.8 + live_spacing * (i + 1)
    v_line(ax, cx, 3.6, 3.25)
    draw_box(ax, cx, 3.0, 1.4, 0.48, child)

# ===== 作业与考试子功能 =====
exam_children = [
    '题库管理\n五种题型',
    '手动/自动\n组卷',
    '在线答题\n自动评分',
    '作业布置\n提交批改',
]
exam_n = len(exam_children)
exam_spacing = 3.6 / (exam_n + 1)
for i, child in enumerate(exam_children):
    cx = 7.4 - 1.8 + exam_spacing * (i + 1)
    v_line(ax, cx, 3.6, 3.25)
    draw_box(ax, cx, 3.0, 1.4, 0.48, child)

# ===== 分工虚线框标注 =====
ax.plot([0.5, 10.0, 10.0, 0.5, 0.5], [5.0, 5.0, 2.3, 2.3, 5.0],
        'k--', lw=0.8)
ax.text(9.5, 2.5, 'Person B 负责', ha='center', va='center',
        fontsize=8, fontweight='bold', color='black',
        bbox=dict(boxstyle='square,pad=0.3', facecolor='white',
                   edgecolor='black', linewidth=0.8, linestyle='--'))

# ===== 图注 =====
ax.text(5.0, 0.15, '图2-2  系统功能架构设计图（Person B）', ha='center', va='center',
        fontsize=10, fontweight='bold', color='black')

plt.tight_layout(pad=0.3)
plt.savefig('/Users/qt/online-edu-platform/docs/diagrams/功能结构图_PersonB.png',
            dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close()
print('Done')
