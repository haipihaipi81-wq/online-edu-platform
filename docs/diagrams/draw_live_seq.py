import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch

plt.rcParams['font.sans-serif'] = ['SimSun', 'STSong', 'Arial Unicode MS', 'Heiti SC']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(10, 8.5))
ax.set_xlim(0, 10)
ax.set_ylim(0, 8.5)
ax.axis('off')

# 五个参与者的 x 坐标
X = {'教师': 1.2, '前端': 3.0, '控制器': 5.4, '业务层': 7.4, '弹幕': 9.2}

def box(ax, x, y, w, h, text, bold=False, fs=8):
    rect = FancyBboxPatch((x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.12", facecolor='white', edgecolor='black', linewidth=1.2)
    ax.add_patch(rect)
    wt = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fs, fontweight=wt, color='black')

def lifeline(ax, x, y1, y2):
    ax.plot([x, x], [y1, y2], color='#999999', lw=0.5, linestyle='--')

# 向右实线箭头
def to_right(ax, x1, x2, y, label):
    ax.annotate('', xy=(x2 - 0.08, y), xytext=(x1 + 0.08, y),
                arrowprops=dict(arrowstyle='->', color='black', lw=1.0))
    if label:
        ax.text((x1 + x2) / 2, y + 0.12, label, ha='center', va='bottom', fontsize=7.5, color='black')

# 向左虚线返回
def to_left(ax, x1, x2, y, label):
    ax.annotate('', xy=(x2 + 0.08, y), xytext=(x1 - 0.08, y),
                arrowprops=dict(arrowstyle='->', color='black', lw=0.8, linestyle='dashed'))
    if label:
        ax.text((x1 + x2) / 2, y - 0.16, label, ha='center', va='top', fontsize=7, color='#555555')

# 自调用
def self_call(ax, x, y, label):
    ax.annotate(label, xy=(x + 0.5, y), xytext=(x + 0.05, y - 0.05),
                arrowprops=dict(arrowstyle='->', color='black', lw=1.0, connectionstyle='arc3,rad=0.45'),
                fontsize=7.5, color='black')

def db_note(ax, x, y, text):
    ax.text(x, y, text, fontsize=7, color='#444444',
            bbox=dict(boxstyle='square,pad=0.2', facecolor='white', edgecolor='#cccccc', linewidth=0.5))

# ===== 顶部参与者框 =====
actors_top = 8.1
for name in ['教师', '前端', '控制器', '业务层', '弹幕']:
    box(ax, X[name], actors_top, 1.5, 0.45, name, bold=True)
    lifeline(ax, X[name], actors_top - 0.22, 0.3)

# ================================================================
# 流程1：创建直播间
# ================================================================
y = 7.2
to_right(ax, X['教师'], X['前端'], y, '1. 填写直播间信息并提交')
to_right(ax, X['前端'], X['控制器'], y - 0.45, 'POST /api/live/rooms')
to_right(ax, X['控制器'], X['业务层'], y - 0.9, 'createRoom()')
db_note(ax, 6.5, y - 1.3, '保存直播间信息到数据库')
to_left(ax, X['业务层'], X['前端'], y - 1.8, '')
to_left(ax, X['控制器'], X['前端'], y - 2.25, '')
to_left(ax, X['前端'], X['教师'], y - 2.7, '返回推拉流地址，创建成功')

# ================================================================
# 流程2：进入直播间（学生）
# ================================================================
y = 4.2
ax.text(X['教师'], y + 0.2, '学生', fontsize=8, fontweight='bold', color='black',
        bbox=dict(boxstyle='square,pad=0.15', facecolor='white', edgecolor='black', linewidth=0.8))
to_right(ax, X['教师'], X['前端'], y - 0.2, '2. 点击进入直播间')
to_right(ax, X['前端'], X['控制器'], y - 0.65, 'GET /api/live/rooms/{id}')
to_right(ax, X['控制器'], X['业务层'], y - 1.1, 'getRoomDetail()')
db_note(ax, 6.5, y - 1.5, '查询数据库获取直播间信息')
to_left(ax, X['业务层'], X['前端'], y - 2.0, '')
to_left(ax, X['控制器'], X['前端'], y - 2.45, '')
to_left(ax, X['前端'], X['教师'], y - 2.9, '返回拉流地址，展示直播画面')

# ================================================================
# 流程3：WebSocket 连接
# ================================================================
y = 1.6
to_right(ax, X['前端'], X['弹幕'], y, '3. 建立 WebSocket 连接（握手请求）')

# ================================================================
# 流程4：弹幕互动
# ================================================================
y = 0.8
to_right(ax, X['教师'], X['前端'], y, '4. 输入弹幕内容并发送')
to_right(ax, X['前端'], X['弹幕'], y - 0.35, '推送弹幕消息帧')
self_call(ax, X['弹幕'], y - 0.6, '广播给房间内所有在线用户')
db_note(ax, 7.8, y - 1.0, '保存聊天记录到数据库')

# ===== 图注 =====
ax.text(5.0, 0.06, '图2-3  直播教学模块时序图', ha='center', va='center',
        fontsize=10, fontweight='bold', color='black')

plt.tight_layout(pad=0.2)
plt.savefig('/Users/qt/online-edu-platform/docs/diagrams/直播教学时序图.png',
            dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close()
print('Done')
