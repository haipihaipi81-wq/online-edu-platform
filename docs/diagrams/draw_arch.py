import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

plt.rcParams['font.sans-serif'] = ['SimSun', 'STSong', 'Arial Unicode MS', 'Heiti SC']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(10, 7.5))
ax.set_xlim(0, 10)
ax.set_ylim(0, 7.5)
ax.axis('off')

def draw_box(ax, x, y, w, h, text, bold=False, lw=1.2):
    rect = mpatches.FancyBboxPatch(
        (x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.15",
        facecolor='white', edgecolor='black', linewidth=lw
    )
    ax.add_patch(rect)
    fs = 8
    wt = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fs, fontweight=wt, color='black')

def draw_dashed_box(ax, x, y, w, h):
    rect = mpatches.FancyBboxPatch(
        (x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.15",
        facecolor='none', edgecolor='black', linewidth=1.0, linestyle='--'
    )
    ax.add_patch(rect)

def arrow_down(ax, x, y1, y2):
    ax.annotate('', xy=(x, y2 + 0.02), xytext=(x, y1 - 0.02),
                arrowprops=dict(arrowstyle='->', color='black', lw=1.2))

def v_line(ax, x, y1, y2):
    ax.plot([x, x], [y1, y2], color='#333333', lw=0.8)

def h_line(ax, x1, x2, y):
    ax.plot([x1, x2], [y, y], color='#333333', lw=0.8)

# ============================================================
# 三层虚线大框
# ============================================================
# 应用层 y: 5.9 ~ 7.2
draw_dashed_box(ax, 5.0, 6.55, 9.0, 0.9)
ax.text(0.6, 6.55, '应\n用\n层', ha='center', va='center', fontsize=9,
        fontweight='bold', color='black')

# 业务逻辑层 y: 2.8 ~ 5.6
draw_dashed_box(ax, 5.0, 4.2, 9.0, 2.4)
ax.text(0.6, 4.2, '业 务 逻 辑 层', ha='center', va='center', fontsize=9,
        fontweight='bold', color='black')

# 持久层 y: 0.5 ~ 2.5
draw_dashed_box(ax, 5.0, 1.5, 9.0, 1.6)
ax.text(0.6, 1.5, '持\n久\n层', ha='center', va='center', fontsize=9,
        fontweight='bold', color='black')

# ============================================================
# 应用层
# ============================================================
draw_box(ax, 5.0, 6.55, 8.2, 0.55, 'Vue 3 + Element Plus + Pinia + Axios（前端 SPA）', bold=True)

# ============================================================
# 业务逻辑层
# ============================================================
draw_box(ax, 5.0, 5.2, 8.2, 0.5, 'Spring Boot 3 RESTful API', bold=True, lw=1.4)

# 四个模块
mod_x = [1.7, 3.9, 6.1, 8.3]
mod_labels = ['用户与认证', '课程与内容\n管理', '直播教学', '作业与考试']

for mx, lb in zip(mod_x, mod_labels):
    draw_box(ax, mx, 4.3, 1.9, 0.7, lb, bold=True)

# 总框到模块的连线
h_line(ax, 1.7, 8.3, 4.95)
for mx in mod_x:
    v_line(ax, mx, 4.95, 4.65)

# 每个模块下的关键组件
mod_sub = {
    1.7: ['AuthController\n登录注册', 'JwtAuthFilter\n令牌校验'],
    3.9: ['CourseController\n课程管理', 'ChapterController\n章节管理'],
    6.1: ['LiveController\n直播管理', 'ChatWebSocket\n实时弹幕'],
    8.3: ['ExamController\n考试管理', 'HomeworkController\n作业管理'],
}

for mx, subs in mod_sub.items():
    v_line(ax, mx, 3.95, 3.65)
    spacing = 1.8 / (len(subs) + 1)
    for i, sub in enumerate(subs):
        cx = mx - 0.7 + spacing * (i + 1)
        v_line(ax, cx, 3.65, 3.4)
        draw_box(ax, cx, 3.15, 0.85, 0.55, sub, lw=1.0)

# 层间箭头
arrow_down(ax, 5.0, 6.27, 5.45)
arrow_down(ax, 5.0, 2.95, 2.3)

# 标注通信协议
ax.text(6.5, 5.85, 'HTTP / JSON', fontsize=7, color='#555555')
ax.text(6.5, 2.6, 'JDBC', fontsize=7, color='#555555')

# ============================================================
# 持久层
# ============================================================
draw_box(ax, 5.0, 2.0, 5.5, 0.55, 'MySQL 8.0 + MyBatis-Plus', bold=True)

# 四组数据表
table_x = [1.7, 3.9, 6.1, 8.3]
table_labels = [
    'user / role\npermission',
    'course / chapter\nsection / category',
    'live_room\nlive_chat',
    'question / exam\nhomework',
]
for mx, lb in zip(table_x, table_labels):
    v_line(ax, mx, 1.72, 1.35)
    draw_box(ax, mx, 1.05, 1.7, 0.45, lb, lw=1.0)

# 总框到表的连线
h_line(ax, 1.7, 8.3, 1.72)
for mx in mod_x:
    v_line(ax, mx, 1.72, 1.72)

# ============================================================
# 图注
# ============================================================
ax.text(5.0, 0.12, '图2-1  系统整体架构设计图', ha='center', va='center',
        fontsize=10, fontweight='bold', color='black')

plt.tight_layout(pad=0.3)
plt.savefig('/Users/qt/online-edu-platform/docs/diagrams/整体架构设计图.png',
            dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close()
print('Done')
